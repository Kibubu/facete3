package org.hobbit.trash;

//public class DockerServiceFactoryUtilsSpringBoot {
//	
////	public static DockerServiceFactory<?> createDockerServiceFactoryForBootstrap(Map<String, Supplier<SpringApplicationBuilder>> imageNameToConfigSupplier) {
////
////		DockerServiceFactory<DockerService> result =
////				new DockerServiceFactorySpringApplicationBuilder<>(
////						imageNameToConfigSupplier,
////						(appBuilder, env) -> {
////							Map<String, Object> env2 = env.entrySet().stream().collect(Collectors.toMap(Entry::getKey, x -> (Object)x.getValue()));
////
////							ConfigurableEnvironment cenv = new StandardEnvironment();
////							cenv.getPropertySources().addFirst(new MapPropertySource("myPropertySource", env2));
////							appBuilder.environment(cenv);
////						},
////						appBuilder -> new ServiceSpringApplicationBuilder(appBuilder));
////	
////		return result;
////	}
//	
//	/**
//	 * This function yields a DockerServiceFactory that
//	 * capable of launching classes using SpringBoot's ApplicationBuilder.
//	 * 
//	 * 
//	 * @param imageNameToClass
//	 * @param baseAppBuilder
//	 * @return
//	 */
//	public static DockerServiceFactory<?> createDockerServiceFactoryForBootstrapOld2(Map<String, Supplier<SpringApplicationBuilder>> imageNameToApplicationBuilderSupplier) {
//
//		DockerServiceFactory<DockerService> result = new DockerServiceFactoryGeneric.Builder<
//					Supplier<SpringApplicationBuilder>,
//					Entry<ConfigurableApplicationContext, Service>,
//					Entry<ConfigurableApplicationContext, Service>>()
//				.setConfigMap(imageNameToApplicationBuilderSupplier)
//				.setStart((env, appBuilderSupplier) -> {
//					Map<String, Object> env2 = env.entrySet().stream().collect(Collectors.toMap(Entry::getKey, x -> (Object)x.getValue()));
//
//					SpringApplicationBuilder appBuilder = appBuilderSupplier.get()
//						.properties(env2);
//					ConfigurableApplicationContext ctx = appBuilder.run();
//				
//					ServiceCapable sc = ctx.getBean(ServiceCapable.class);
//					
//					Service service = ServiceCapableWrapper.wrap(sc);
//					service.startAsync().awaitRunning();
//					
//					return new SimpleEntry<>(ctx, service);
//				})
//				.setRun(e -> e)
//				.setStop((appBuilder, ctxAndService) -> {
//					if(ctxAndService != null) {
//						try {
//							ConfigurableApplicationContext ctx = ctxAndService.getKey();
//							Service service = ctxAndService.getValue();
//							service.stopAsync().awaitTerminated();
//							ctx.close(); 
//						} catch(Exception e) {
//							throw new RuntimeException(e);
//						}
//					}})
//				.build();
//		return result;
//	}
//
//	
//	public static DockerServiceFactory<?> createDockerServiceFactoryForBootstrapOld(Map<String, Supplier<SpringApplicationBuilder>> imageNameToApplicationBuilderSupplier) {
//
//		DockerServiceFactory<DockerService> result = new DockerServiceFactoryGeneric.Builder<
//					Supplier<SpringApplicationBuilder>,
//					Entry<Map<String, String>, Supplier<SpringApplicationBuilder>>,
//					ConfigurableApplicationContext>()
//				.setConfigMap(imageNameToApplicationBuilderSupplier)
//				.setStart((env, appBuilderSupplier) -> new SimpleEntry<>(env, appBuilderSupplier))
//				.setRun(e -> {
//					Map<String, Object> env = e.getKey().entrySet().stream().collect(Collectors.toMap(Entry::getKey, x -> (Object)x.getValue()));
//
//					SpringApplicationBuilder appBuilder = e.getValue().get()
//						.properties(env);
//						
////					
////					ConfigurableEnvironment cenv = new StandardEnvironment();
////					cenv.getPropertySources().addFirst(new MapPropertySource("myPropertySource", env));
//
//					
//					ConfigurableApplicationContext r = appBuilder.run();
//					return r;
//				})
//				.setStop((appBuilder, ctx) -> {
//					if(ctx != null) {
//						try {
//							ctx.close(); 
//						} catch(Exception e) {
//							throw new RuntimeException(e);
//						}
//					}})
//				.build();
//		return result;
//	}
//}
