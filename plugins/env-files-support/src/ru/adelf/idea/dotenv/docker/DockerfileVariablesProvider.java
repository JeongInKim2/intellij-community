package ru.adelf.idea.dotenv.docker;

import com.intellij.docker.dockerFile.DockerFileType;
import com.intellij.docker.dockerFile.DockerPsiFile;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import ru.adelf.idea.dotenv.api.EnvironmentVariablesProvider;
import ru.adelf.idea.dotenv.models.KeyValuePsiElement;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class DockerfileVariablesProvider implements EnvironmentVariablesProvider {
    @Override
    public boolean acceptFile(VirtualFile file) {
        return file.getFileType().equals(DockerFileType.DOCKER_FILE_TYPE);
    }

    @NotNull
    @Override
    public Collection<KeyValuePsiElement> getElements(PsiFile psiFile) {
        if(psiFile instanceof DockerPsiFile) {
            DockerfilePsiElementsVisitor visitor = new DockerfilePsiElementsVisitor();
            psiFile.acceptChildren(visitor);

            return visitor.getCollectedItems();
        }

        return Collections.emptyList();
    }
}
