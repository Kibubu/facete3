package org.hobbit.service.podigg;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.aksw.commons.service.core.SimpleProcessExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PodiggWrapper {

    private static final Logger logger = LoggerFactory.getLogger(PodiggWrapper.class);

    // GTFS_GEN_SEED=111 -e GTFS_GEN_REGION__SIZE_X=2000  -e GTFS_GEN_REGION__SIZE_Y=2000 -e GTFS_GEN_REGION__CELLS_PER_LATLON=200 -e GTFS_GEN_STOPS__STOPS=3500 -e GTFS_GEN_CONNECTIONS__DELAY_CHANCE=0.02 -e GTFS_GEN_CONNECTIONS__CONNECTIONS=4000 -e GTFS_GEN_ROUTES__ROUTES=3500 -e GTFS_GEN_ROUTES__MAX_ROUTE_LENGTH=50 -e GTFS_GEN_ROUTES__MIN_ROUTE_LENGTH=10  -e GTFS_GEN_CONNECTIONS__ROUTE_CHOICE_POWER=1.3  -e GTFS_GEN_CONNECTIONS__TIME_FINAL=31536000000

    public static void exec(String basePath, Path outputFolder, Map<String, String> env) throws IOException, InterruptedException {
        String cmd = basePath + "/bin/generate-env";

        ProcessBuilder processBuilder = new ProcessBuilder(cmd, outputFolder.toAbsolutePath().toString());
        Map<String, String> penv = processBuilder.environment();
        penv.putAll(env);

        SimpleProcessExecutor.wrap(processBuilder)
            .setOutputSink(logger::info) //System.out::println) //logger::debug)
            .execute();
    }

    

//    public static Stream<Triple> springBatchTest() throws IOException, InterruptedException {
//    	
//    	String podiggHomePath = "/home/raven/Projects/Eclipse/podigg-lc-bin";
//    	String basename = "podigg";
//    	
//    	JobParameters params = new JobParametersBuilder()
//    			.addString("GTFS_GEN_SEED", "123")
//    			.toJobParameters();
//    			
//    	
//        //Map<String, String> params = new HashMap<String, String>();
//        //params.put("GTFS_GEN_SEED", "123");
//
//    	Map<String, JobParameter> map = params.getParameters();
//    	
//    	Map<String, String> identifyingParams = map.entrySet().stream().filter(e -> e.getValue().isIdentifying()).collect(Collectors.toMap(
//    			e -> Objects.toString(e.getKey()), e -> Objects.toString(e.getValue()),
//    			(u, v) -> u, TreeMap::new));
//    	
//    	String dirname;
//    	try {
//    		dirname = identifyingParams.entrySet().stream().map(Object::toString).collect(Collectors.joining("_"));
//    		
//			dirname = dirname.replaceAll("[=\\./]", "-");
//			dirname = URLEncoder.encode(dirname, StandardCharsets.UTF_8.name());
//			dirname = basename + "-" + dirname;
//		} catch (UnsupportedEncodingException e1) {
//			throw new RuntimeException(e1);
//		}
//
//        Path baseFolder = Paths.get("/tmp/podigg");
//        baseFolder.toFile().mkdirs();
//    	
//
//    	Map<String, String> env = map.entrySet().stream().collect(Collectors.toMap(
//    			e -> Objects.toString(e.getKey()), e -> Objects.toString(e.getValue()),
//    			(u, v) -> u, TreeMap::new));
//    	
//
//    	Path outputFolder = baseFolder.resolve(dirname);
//    	if(!outputFolder.toFile().exists()) {
//
//    		Path preparationFolder = baseFolder.resolve("preparation-" + dirname);
//    		preparationFolder.toFile().mkdir();
//    		
//    		//preparationFolder.toFile().deleteOnExit();
//    		
//    		//outputFolder.toFile().mkdir();
//    		
//    		// Create a lock file
//    		File lockFile = preparationFolder.resolve(".lock").toFile();
//    		lockFile.deleteOnExit();
//    		try(RandomAccessFile raFile = new RandomAccessFile(lockFile, "rw")) {
//    			FileChannel channel = raFile.getChannel();
//    			
//	    	    try {
//	    	    	FileLock lock = channel.tryLock();
//	    	       
//	    	    	try {
//	    	    		exec(podiggHomePath, preparationFolder, env);
//	    	    	} finally {
//		    	    	lock.release();
//		    	    	lockFile.delete();
//		    	    }	    	       
//	    	    } catch (OverlappingFileLockException e) {
//	    	    	throw new RuntimeException("Lock file " + lockFile.getAbsolutePath() + " exists; delete this file once ensured no other process is writing to this directory");
//	    	    }
//    		}
//    	
//    		FileSystemUtils.deleteRecursively(outputFolder.toFile());
//    		preparationFolder.toFile().renameTo(outputFolder.toFile());    		
//    	}
//    	
//        //FileSystemUtils.deleteRecursively(outputFolder.toFile());
//
//        Path datasetFile = outputFolder.resolve("lc.ttl");
//
//        Stream<Triple> result = GraphUtils.createTripleStream(datasetFile.toString());
//
//        return result;
//    }


//    public static void main(String[] args) throws IOException, InterruptedException {
//        System.out.println("Triples: " + test().count());
//    }
}
