package org.hobbit.benchmark.faceted_browsing.main;

//public class MainHobbitFacetedeBrowsingBenchmarkLocal {
//	
//	private static final Logger logger = LoggerFactory.getLogger(MainHobbitFacetedeBrowsingBenchmarkLocal.class);
//	
//    public static void main(String[] args) throws Exception {
//
//    	logger.debug("Benchmark startup initiated");
//    	//start();
//    	
//    	// The platform ensures that the system under test (sut) is
//    	// ready before start up of the benchmark controller
//    	// however, this may change in the future, such that the sut is
//    	// only launched after all data and task generation preparation is complete.
//    	
//    	
//    	// The system adapter is comprised of two components:
//    	// The hobbit client which acts as a bridge between the task generator and the system under test
//    	// The docker container that wraps both components
//    	// Hence, either the container or the hobbit client can bring up the actual system under test.
//    	
//    	
//        try(AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
//                ConfigDockerServiceFactory.class,
//                HobbitConfigChannelsLocal.class,
//                ConfigFacetedBenchmarkV1LocalServices.class)) {
//
//        	
//        	Service systemUnderTestService = null;
//        	try {
//        		systemUnderTestService = (Service)ctx.getBean("systemUnderTestService");
//        		systemUnderTestService.startAsync();
//        		systemUnderTestService.awaitRunning();
//        		
//// The system adapter has to send out the system_ready_signal
////        		WritableByteChannel commandChannel = (WritableByteChannel)ctx.getBean("commandChannel");
////        		commandChannel.write(ByteBuffer.wrap(new byte[] {Commands.SYSTEM_READY_SIGNAL}));
//        		
//        		
//	        	Supplier<Service> systemAdapterServiceFactory = (Supplier<Service>)ctx.getBean("systemAdapterServiceFactory");
//	        	Service systemAdapter = systemAdapterServiceFactory.get();
//	        	systemAdapter.startAsync();
//	        	systemAdapter.awaitRunning();
//	        	
//	        	
//	            PseudoHobbitPlatformController commandHandler = ctx.getBean(PseudoHobbitPlatformController.class);
//	            commandHandler.accept(ByteBuffer.wrap(new byte[] {Commands.START_BENCHMARK_SIGNAL}));
//	            
//	            // Sending the command blocks until the benchmark is complete
//	            //System.out.println("sent start benchmark signal");
//	            
//        	} catch(Exception e) {
//        		throw new RuntimeException(e);
//        	} finally {
//                if(systemUnderTestService != null) {
//                    logger.debug("Shutting down system under test service");
//                    systemUnderTestService.stopAsync();
//                    ServiceManagerUtils.awaitTerminatedOrStopAfterTimeout(systemUnderTestService, 60, TimeUnit.SECONDS, 0, TimeUnit.SECONDS);
////                  systemUnderTestService.stopAsync();
////                  systemUnderTestService.awaitTerminated(60, TimeUnit.SECONDS);
//                }        	
//        	}
//        }
//
//    }
//}
