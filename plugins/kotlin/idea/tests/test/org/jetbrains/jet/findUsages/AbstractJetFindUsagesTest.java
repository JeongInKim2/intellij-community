/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.findUsages;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.intellij.find.FindManager;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.impl.FindManagerImpl;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.intellij.usages.rules.UsageFilteringRule;
import com.intellij.util.ArrayUtil;
import com.intellij.util.CommonProcessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.InTextDirectivesUtils;
import org.jetbrains.jet.lang.psi.JetClass;
import org.jetbrains.jet.lang.psi.JetFunction;
import org.jetbrains.jet.lang.psi.JetObjectDeclarationName;
import org.jetbrains.jet.lang.psi.JetProperty;
import org.jetbrains.jet.plugin.JetLightProjectDescriptor;
import org.jetbrains.jet.plugin.PluginTestCaseBase;
import org.jetbrains.jet.plugin.findUsages.JetImportFilteringRule;
import org.jetbrains.jet.plugin.findUsages.options.KotlinMethodFindUsagesOptions;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractJetFindUsagesTest extends LightCodeInsightFixtureTestCase {
    private interface OptionsParser {
        @NotNull
        FindUsagesOptions parse(@NotNull String text, @NotNull Project project);
    }

    private static final OptionsParser METHOD_OPTIONS_PARSER = new OptionsParser() {
        @Override
        @NotNull
        public FindUsagesOptions parse(@NotNull String text, @NotNull Project project) {
            KotlinMethodFindUsagesOptions options = new KotlinMethodFindUsagesOptions(project);
            options.isUsages = false;
            for (String s : InTextDirectivesUtils.findListWithPrefixes(text, "// OPTIONS: ")) {
                if (s.equals("usages")) {
                    options.isUsages = true;
                }

                if (s.equals("overrides")) {
                    options.isOverridingMethods = true;
                    options.isImplementingMethods = true;
                }

                if (s.equals("overloadUsages")) {
                    options.isIncludeOverloadUsages = true;
                    options.isUsages = true;
                }
            }

            return options;
        }
    };

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return JetLightProjectDescriptor.INSTANCE;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.setTestDataPath(PluginTestCaseBase.getTestDataPathBase() + "/findUsages");
    }

    public void testFindClassJavaUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, true, JetClass.class, null);
    }

    public void testFindClassKotlinUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, false, JetClass.class, null);
    }

    public void testFindUsagesUnresolvedAnnotation(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, true, JetClass.class, null);
    }

    public void testFindMethodJavaUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, true, JetFunction.class, METHOD_OPTIONS_PARSER);
    }

    public void testFindMethodKotlinUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, false, JetFunction.class, METHOD_OPTIONS_PARSER);
    }

    public void testFindPropertyJavaUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, true, JetProperty.class, null);
    }

    public void testFindPropertyKotlinUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, false, JetProperty.class, null);
    }

    public void testFindObjectJavaUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, true, JetObjectDeclarationName.class, null);
    }

    public void testFindObjectKotlinUsages(@NotNull String path) throws Exception {
        doTestWithoutFiltering(path, false, JetObjectDeclarationName.class, null);
    }

    public void testFindWithFilteringImports(@NotNull String path) throws Exception {
        doTest(path, false, JetClass.class, Lists.newArrayList(new JetImportFilteringRule()), null);
    }

    private <T extends PsiElement> void doTestWithoutFiltering(
            @NotNull String path,
            boolean searchInJava,
            @NotNull Class<T> caretElementClass,
            @Nullable OptionsParser parser
    ) throws Exception {
        doTest(path, searchInJava, caretElementClass, Collections.<UsageFilteringRule>emptyList(), parser);
    }

    private <T extends PsiElement> void doTest(
            @NotNull String path,
            boolean searchInJava,
            @NotNull Class<T> caretElementClass,
            @NotNull Collection<? extends UsageFilteringRule> filters,
            @Nullable OptionsParser parser
    ) throws IOException {
        String rootPath = path.substring(0, path.lastIndexOf("/") + 1);

        myFixture.configureByFiles(path, rootPath + "Client." + (searchInJava ? "java" : "kt"));
        T caretElement = PsiTreeUtil.getParentOfType(myFixture.getElementAtCaret(), caretElementClass, false);
        assertNotNull(String.format("Element with type '%s' wasn't found at caret position", caretElementClass), caretElement);

        FindUsagesOptions options = parser != null ? parser.parse(FileUtil.loadFile(new File(path)), getProject()) : null;
        Collection<UsageInfo> usageInfos = findUsages(caretElement, options);

        Collection<UsageInfo2UsageAdapter> filteredUsages = getUsageAdapters(filters, usageInfos);

        Function<UsageInfo2UsageAdapter, String> convertToString = new Function<UsageInfo2UsageAdapter, String>() {
            @Override
            public String apply(@Nullable UsageInfo2UsageAdapter usageAdapter) {
                assert usageAdapter != null;
                return getUsageType(usageAdapter.getElement())
                       + " " + Joiner.on("").join(Arrays.asList(usageAdapter.getPresentation().getText()));
            }
        };

        Collection<String> finalUsages = Ordering.natural().sortedCopy(Collections2.transform(filteredUsages, convertToString));
        String expectedText = FileUtil.loadFile(new File(rootPath + "results.txt"), true);
        assertOrderedEquals(finalUsages, Ordering.natural().sortedCopy(StringUtil.split(expectedText, "\n")));
    }

    private Collection<UsageInfo> findUsages(@NotNull PsiElement targetElement, @Nullable FindUsagesOptions options) {
        Project project = getProject();
        FindUsagesHandler handler =
                ((FindManagerImpl) FindManager.getInstance(project)).getFindUsagesManager().getFindUsagesHandler(targetElement, false);
        assert handler != null : "Cannot find handler for: " + targetElement;

        if (options == null) {
            options = handler.getFindUsagesOptions(null);
        }

        CommonProcessors.CollectProcessor<UsageInfo> processor = new CommonProcessors.CollectProcessor<UsageInfo>();
        PsiElement[] psiElements = ArrayUtil.mergeArrays(handler.getPrimaryElements(), handler.getSecondaryElements());

        for (PsiElement psiElement : psiElements) {
            handler.processElementUsages(psiElement, processor, options);
        }

        return processor.getResults();
    }

    private static Collection<UsageInfo2UsageAdapter> getUsageAdapters(
            final Collection<? extends UsageFilteringRule> filters,
            Collection<UsageInfo> usageInfos
    ) {
        return Collections2.filter(
                Collections2.transform(usageInfos, new Function<UsageInfo, UsageInfo2UsageAdapter>() {
                    @Override
                    public UsageInfo2UsageAdapter apply(@Nullable UsageInfo usageInfo) {
                        assert (usageInfo != null);

                        UsageInfo2UsageAdapter usageAdapter = new UsageInfo2UsageAdapter(usageInfo);
                        for (UsageFilteringRule filter : filters) {
                            if (!filter.isVisible(usageAdapter)) {
                                return null;
                            }
                        }

                        return usageAdapter;
                    }
                }),
                Predicates.notNull());
    }

    @Nullable
    private static UsageType getUsageType(PsiElement element) {
        if (element == null) return null;

        if (PsiTreeUtil.getParentOfType(element, PsiComment.class, false) != null) {
            return UsageType.COMMENT_USAGE;
        }

        UsageTypeProvider[] providers = Extensions.getExtensions(UsageTypeProvider.EP_NAME);
        for (UsageTypeProvider provider : providers) {
            UsageType usageType = provider.getUsageType(element);
            if (usageType != null) {
                return usageType;
            }
        }

        return UsageType.UNCLASSIFIED;
    }
}
