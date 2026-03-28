package vkr.verifier.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vkr.verifier.config.properties.VerifierProperties;
import vkr.verifier.exception.ExecutionVerifierScriptException;

@Service
@RequiredArgsConstructor
public class VerifierClient {
    private final VerifierProperties verifierProperties;

    public String run(Path filePath) {
        Path appBaseDir = Path.of(System.getProperty("user.dir"))
            .toAbsolutePath()
            .normalize();
        Path workingDirPath = resolveAgainstAppBase(
            appBaseDir,
            verifierProperties.getWorkingDir()
        );
        Path pythonExecutablePath = resolveAgainstAppBase(
            appBaseDir,
            verifierProperties.getPythonExecutable()
        );
        validateWorkingDir(workingDirPath);
        validatePythonExecutable(pythonExecutablePath);
        validateInputFile(filePath);
        ProcessBuilder processBuilder = new ProcessBuilder(
            pythonExecutablePath.toString(),
            "-m",
            verifierProperties.getModuleName(),
            filePath.toAbsolutePath().normalize().toString()
        );
        processBuilder.directory(workingDirPath.toFile());
        processBuilder.redirectErrorStream(true);
        try {
            Process process = processBuilder.start();
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
            Thread outputReader = new Thread(
                () -> copyProcessOutput(process, outputBuffer)
            );
            outputReader.setDaemon(true);
            outputReader.start();
            boolean finished = process.waitFor(
                verifierProperties.getTimeoutSeconds(),
                TimeUnit.SECONDS
            );
            if (!finished) {
                process.destroyForcibly();
                try {
                    process.waitFor(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                throw new ExecutionVerifierScriptException("Python verifier timed out");
            }
            outputReader.join(5000);
            String output = outputBuffer.toString(StandardCharsets.UTF_8);
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new ExecutionVerifierScriptException(
                    "Python verifier failed. Exit code=" + exitCode + ", output=" + output
                );
            }
            return output;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionVerifierScriptException("Python verifier execution was interrupted");
        } catch (IOException e) {
            throw new ExecutionVerifierScriptException("Failed to start python verifier");
        }
    }

    private Path resolveAgainstAppBase(Path appBaseDir, String relativeOrAbsolutePath) {
        return appBaseDir.resolve(relativeOrAbsolutePath)
            .normalize()
            .toAbsolutePath();
    }

    private void validateWorkingDir(Path workingDirPath) {
        if (!Files.exists(workingDirPath) || !Files.isDirectory(workingDirPath)) {
            throw new ExecutionVerifierScriptException(
                "Verifier working directory does not exist: " + workingDirPath
            );
        }
    }

    private void validatePythonExecutable(Path pythonExecutablePath) {
        if (!Files.exists(pythonExecutablePath) || !Files.isRegularFile(pythonExecutablePath)) {
            throw new ExecutionVerifierScriptException(
                "Python executable does not exist: " + pythonExecutablePath
            );
        }
        if (!Files.isExecutable(pythonExecutablePath)) {
            throw new ExecutionVerifierScriptException(
                "Python executable is not executable: " + pythonExecutablePath
            );
        }
    }

    private void validateInputFile(Path filePath) {
        if (filePath == null) {
            throw new ExecutionVerifierScriptException("Input file path is null");
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ExecutionVerifierScriptException(
                "Input file does not exist: " + filePath
            );
        }
    }

    private void copyProcessOutput(Process process, ByteArrayOutputStream outputBuffer) {
        try (InputStream inputStream = process.getInputStream()) {
            inputStream.transferTo(outputBuffer);
        } catch (IOException ignored) {}
    }
}
