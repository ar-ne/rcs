package ar.ne.rcs.server.forward;

import ar.ne.rcs.shared.models.frp.FRPServer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class ForwardFRPServer extends AbstractForwardServer {
    @Value("${forward.server.frps.token}")
    String token;

    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();
    DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

    @Override
    String getServerType() {
        return "frp";
    }

    @Override
    void startup(int port) {
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("rcs-server-frp");
            //write frps.ini
            Files.write(tempDir.resolve("frps.ini"), FRPServer.builder()
                    .common(FRPServer.Common.builder()
                            .token(token)
                            .build())
                    .build()
                    .toIni().getBytes(StandardCharsets.UTF_8));

            //copy Dockerfile
            Files.copy(Paths.get(new ClassPathResource("./raw/frps.Dockerfile").getURI()), tempDir.resolve("Dockerfile"));

            //build image
            BuildImageResultCallback imgBuildCallback = new BuildImageResultCallback();
            dockerClient.buildImageCmd(tempDir.toFile()).exec(imgBuildCallback);

            HostConfig config = HostConfig.newHostConfig().withPortBindings(PortBinding.parse("20545:20545"));
            CreateContainerResponse resp = dockerClient.createContainerCmd(imgBuildCallback.awaitImageId()).withHostConfig(config).exec();
            dockerClient.startContainerCmd(resp.getId()).exec();
        } catch (IOException e) {
            log.error("Failed to write file.");
            e.printStackTrace();
        } finally {
            if (tempDir != null && tempDir.toFile().delete()) {
                log.debug("Temporary {} deleted", tempDir.toFile().getAbsolutePath());
            }
        }

    }

    @Override
    void shutdown() {

    }
}
