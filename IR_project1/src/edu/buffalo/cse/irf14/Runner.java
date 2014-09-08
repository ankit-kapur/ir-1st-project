package edu.buffalo.cse.irf14;

import java.io.File;
import java.util.Date;
import edu.buffalo.cse.irf14.document.Document;
import edu.buffalo.cse.irf14.document.Parser;
import edu.buffalo.cse.irf14.document.ParserException;
import edu.buffalo.cse.irf14.index.IndexWriter;
import edu.buffalo.cse.irf14.index.IndexerException;

/**
 * @author nikhillo
 *
 */
public class Runner {

	/**
	 * 
	 */
	public Runner() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long startTime = new Date().getTime();

//		String ipDir = args[0];
//		String indexDir = args[1];
		// more? idk!
		
//		String ipDir = "C:\\Users\\ankit.kapur\\Desktop\\Study material\\newsindexer-master\\news_training\\training\\ankit_test";
		String ipDir = "C:\\Users\\ankit.kapur\\Desktop\\Study material\\newsindexer-master\\news_training\\training";
		String indexDir = "C:\\Users\\ankit.kapur\\Desktop\\Study material\\newsindexer-master\\news_training\\indexdata";

		File ipDirectory = new File(ipDir);
		String[] catDirectories = ipDirectory.list();

		String[] files;
		File dir;
		int fileCount = 0;

		Document d = null;
		IndexWriter writer = new IndexWriter(indexDir);

		try {
			for (String cat : catDirectories) {
				dir = new File(ipDir + File.separator + cat);
				files = dir.list();

				System.out.println("Inside directory: " + cat);
				if (files == null) {
					System.out.println("\tNo files in directory " + cat);
					continue;
				}

				for (String f : files) {
					try {
						d = Parser.parse(dir.getAbsolutePath() + File.separator + f);
						writer.addDocument(d);
						fileCount++;
					} catch (ParserException e) {
						// TODO
						// Auto-generated
						// catch block
						e.printStackTrace();
					}
				}
			}
			System.out.println("\n\n" + fileCount + " files parsed in this directory.");
			System.out.println("Errors: " + Parser.errorCount);
			
			System.out.println("\ntitleCount: " + Parser.titleCount);
			System.out.println("authorCount: " + Parser.authorCount);
			System.out.println("dateCount: " + Parser.dateCount);
			System.out.println("placeCount: " + Parser.placeCount);
			System.out.println("contentCount: " + Parser.contentCount);
			System.out.println("ReadingTime: " + Parser.counttime);
			
			writer.close();

			System.out.println("\nTime for execution ==> " + (new Date().getTime() - startTime) / 1000.0 + " seconds");
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}