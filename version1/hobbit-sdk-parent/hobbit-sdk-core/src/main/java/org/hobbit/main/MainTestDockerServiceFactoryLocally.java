package org.hobbit.main;

//public class MainTestDockerServiceFactoryLocally {
//    public static void main(String[] args) throws DockerCertificateException, InterruptedException, TimeoutException {
//        DockerClient dockerClient = DefaultDockerClient.fromEnv().build();
//
//
////        DefaultDockerClient.builder().s
//
//        // Bind container port 443 to an automatically allocated available host
//        // port.
//        String[] ports = { "80", "22" };
//        Map<String, List<PortBinding>> portBindings = new HashMap<>();
//        for (String port : ports) {
//            List<PortBinding> hostPorts = new ArrayList<>();
//            hostPorts.add(PortBinding.of("0.0.0.0", port));
//            portBindings.put(port, hostPorts);
//        }
//
//        List<PortBinding> randomPort = new ArrayList<>();
//        randomPort.add(PortBinding.randomPort("0.0.0.0"));
//        portBindings.put("443", randomPort);
//
//        HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();
//
//        ContainerConfig.Builder containerConfigBuilder = ContainerConfig.builder()
//                .hostConfig(hostConfig);
//
//        DockerServiceBuilderDockerClient dockerServiceFactory = new DockerServiceBuilderDockerClient(
//        		dockerClient, containerConfigBuilder, true, null
//        		);
//
////        	    .image("busybox").exposedPorts(ports)
////        	    .cmd("sh", "-c", "while :; do sleep 1; done")
//
//        DockerService service = dockerServiceFactory
//            .setDockerClient(dockerClient)
//            .setContainerConfigBuilder(containerConfigBuilder)
//            .setImageName("busybox")
//            .get();
//
//        Map<String, String> env = ImmutableMap.<String, String>builder()
//                .put("foo", "bar")
//                .put("baz", "")
//                .build();
//
//        dockerServiceFactory.setLocalEnvironment(env);
//
//
//
//
//        service.startAsync();
//        service.awaitRunning(60, TimeUnit.SECONDS);
//
//        System.out.println("Image name: " + service.getImageName());
//        System.out.println("Container Id: " + service.getContainerId());
//        System.out.println("Env: " + dockerServiceFactory.getLocalEnvironment());
//
//        Thread.sleep(5000);
//
//        service.stopAsync();
//        service.awaitTerminated(60, TimeUnit.SECONDS);
//    }
//}
