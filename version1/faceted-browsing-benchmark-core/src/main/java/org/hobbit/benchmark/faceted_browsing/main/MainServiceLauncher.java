package org.hobbit.benchmark.faceted_browsing.main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.aksw.commons.collections.trees.TreeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.Listener;
import com.google.common.util.concurrent.Service.State;


public class MainServiceLauncher {
	private static final Logger logger = LoggerFactory.getLogger(MainServiceLauncher.class);
			
	@Bean
	public ApplicationRunner serviceLauncher(@Qualifier("MainService") Service activeService, ConfigurableApplicationContext ctx) {
		ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)TreeUtils.findRoot(ctx, ApplicationContext::getParent);
//		ConfigurableApplicationContext rootCtx = ctx;

		// Add a listener that closes the service's (root) context on service termination
		activeService.addListener(new Listener() {
            @Override
            public void failed(State priorState, Throwable t) {
                logger.info("ServiceCapable service wrapped [FAILED] for " + (activeService == null ? "(no active service)" : activeService.getClass()), t);
//              logger.info("ServiceCapable service wrapper stopped");
//                  ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)getRoot(ctx.getParent(), ApplicationContext::getParent);
                rootCtx.close();
            }

		    @Override
			public void terminated(State priorState) {
				logger.info("ServiceCapable service wrapper [TERMINATED] for " + (activeService == null ? "(no active service)" : activeService.getClass()));
//				logger.info("ServiceCapable service wrapper stopped");
//					ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)getRoot(ctx.getParent(), ApplicationContext::getParent);
				rootCtx.close();
			}
		}, MoreExecutors.directExecutor());

		
		// If the service's context gets closed, terminate the service
		ctx.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
			@Override
			public void onApplicationEvent(ContextClosedEvent event) {
				logger.info("Context is closing - shutdown service " + (activeService == null ? "(no active service)" : activeService.getClass()));
				if(activeService != null && activeService.isRunning()) {
					try {
						activeService.stopAsync().awaitTerminated(1000, TimeUnit.SECONDS);
//							thread.interrupt();
					} catch (TimeoutException e) {						
						//e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}				
		});
		
		return args -> {
			logger.info("MainServiceLauncher::ApplicationRunner starting service... " + (activeService == null ? "(no active service)" : activeService.getClass()));

			//activeService.startAsync().awaitRunning();
			activeService.startAsync().awaitTerminated();
			logger.info("MainServiceLauncher::ApplicationRunner service started... " + (activeService == null ? "(no active service)" : activeService.getClass()));
		};
	}
}

//thread = new Thread(() -> {
//logger.info("Launching component: " + serviceCapable.getClass());
//
//try {
//	activeService = ServiceCapableWrapper.wrap(serviceCapable);
//	
//	// Add a listener that closes the context on service termination
//	activeService.addListener(new Listener() {
//		@Override
//		public void terminated(State priorState) {
//			logger.info("ServiceCapable service wrapper stopped");
//			ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext)getRoot(ctx.getParent(), ApplicationContext::getParent);
//			rootCtx.close();
//		}
//	}, MoreExecutors.directExecutor());
//
//	activeService.startAsync().awaitRunning();
//	activeService.awaitTerminated();
//	
//	// The services shut themselves down when they are finished
//	//logger.info("Waiting for service termination");
//	//activeService.awaitTerminated();
//	logger.info("Component terminated: " + serviceCapable.getClass());
//	//thread = null;
//} catch(Exception e) {
//	throw new RuntimeException("Component failed: " + serviceCapable.getClass(), e);
//}
//});
//thread.start();
//};
//}



//@Bean
//public Lifecycle componentService(ServiceCapable serviceCapable) {
//Service service = ServiceCapableWrapper.wrap(serviceCapable);
//return new LifecycleService<>(service);
//}

//protected Thread thread;
//protected ServiceDelegate<ServiceCapable> activeService;




//@EventListener
//public void handleContextClosedEvent(ContextClosedEvent event) throws TimeoutException {
//@Override
//public void onApplicationEvent(ContextClosedEvent event) {
//
//logger.info("Got request to shutdown service " + (activeService == null ? "(no active service)" : activeService.getEntity().getClass()));
//if(activeService != null) {
//try {
//activeService.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);
////thread.interrupt();
//} catch (TimeoutException e) {
//throw new RuntimeException(e);
//}
//activeService = null;
//}
//}
