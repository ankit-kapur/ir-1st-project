/**
 * 
 */
package edu.buffalo.cse.irf14.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nikhillo Class that parses a given file into a Document
 */
public class Parser {
	/**
	 * Static method to parse the given file into the Document object
	 * 
	 * @param fileName
	 *             : The fully qualified filename to be parsed
	 * @return The parsed and fully loaded Document object
	 * @throws FilterException
	 *              In case any error occurs during parsing
	 */

	public static int errorCount = 0;
	public static Map<String, String> tempMap;
	public static int titleCount = 0, authorCount = 0, authorOrg = 0, dateCount = 0, placeCount = 0, contentCount = 0;

	public static Document parse(String fileName) throws ParserException {
		String fileId = null, category = null, title = null, author = null, authorOrg = null, newsDate = null, place = null, content = null;
		Document document = new Document();

		try {
			File file = null;
			if (fileName == null || fileName.equals("")) {
				throw new ParserException();
			} else {
				file = new File(fileName);
				if (!file.exists()) {
					throw new ParserException();
				}
			}

			int lastPointerPosition = 0;

			/* Read file's body into a single string */
			String fileBody = null;
			try {
				fileBody = new Scanner(file).useDelimiter("\\A").next();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* File ID */
			int fileNamePosition = fileName.lastIndexOf("\\") >= 0 ? fileName.lastIndexOf("\\") + 1 : fileName.lastIndexOf("/") + 1;
			fileId = fileName.substring(fileNamePosition);

			/* Category ID */
			int categoryPosition = fileName.substring(0, fileNamePosition - 1).lastIndexOf("\\") >= 0 ? fileName.substring(0, fileNamePosition - 1).lastIndexOf("\\") + 1 : fileName.substring(0, fileNamePosition - 1).lastIndexOf("/") + 1;
			category = fileName.substring(categoryPosition, fileNamePosition - 1);

			/* Title */
			Pattern pattern = Pattern.compile("[\\r\\n\\s]*");
			Matcher matcher = pattern.matcher(fileBody);
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile(".*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
				if (matcher.find()) {
					title = matcher.group();
					
					/* Remove any new line characters */
					if (title != null) {
						title = title.replaceAll("[\\n\\r]+", " ");
					}
					
					titleCount++;
					lastPointerPosition += matcher.end();
				}
			}

			/* Author */
			pattern = Pattern.compile("[\\r\\n\\s]*");
			matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile("<[aA][uU][tT][hH][oO][rR]>\\s*[bB][yY]\\s*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
				if (matcher.find()) {
					lastPointerPosition += matcher.end();
					pattern = Pattern.compile("(.+?)</AUTHOR>\\s*");
					matcher = pattern.matcher(fileBody.substring(lastPointerPosition));

					if (matcher.find()) {
						author = matcher.group().substring(0, matcher.group().indexOf("<"));
						if (author != null) {
							/* Remove any new line characters */
							author = author.replaceAll("[\\n\\r]+", " ");
							
							/* Find author orgs */
							if (author.contains(",")) {
								authorOrg = author.substring(author.indexOf(",") + 1).trim();
								author = author.substring(0, author.indexOf(","));
							}
							authorCount++;
							lastPointerPosition += matcher.end();
						}
					}
				}
			}

			/* Date */
			int dateStartPosition = -1, dateEndPosition = -1;
			pattern = Pattern.compile("[\\r\\n\\s]*");
			matcher = pattern.matcher(fileBody.substring(lastPointerPosition));
			if (matcher.find()) {
				lastPointerPosition += matcher.end();
				pattern = Pattern.compile("(jan(uary)?|feb(ruary)?|mar(ch)?|apr(il)?|may|jun(e)?|jul(y)?|aug(ust)?|sep(t(ember)?)?|oct(ober)?|nov(ember)?|dec(ember)?)\\s*[\\d]*");
				matcher = pattern.matcher(fileBody.substring(lastPointerPosition).toLowerCase());

				if (matcher.find()) {
					dateStartPosition = matcher.start() + lastPointerPosition;
					dateEndPosition = matcher.end() + lastPointerPosition;
					newsDate = fileBody.substring(dateStartPosition, dateEndPosition);
					
					/* Remove any new line characters */
					if (newsDate != null) {
						newsDate = newsDate.replaceAll("[\\n\\r]+", " ");
					}
					
					dateCount++;

					/* Place */
					if (fileBody.substring(lastPointerPosition, dateStartPosition).lastIndexOf(",") >= 0) {
						int placeEndPosition = fileBody.substring(lastPointerPosition, dateStartPosition).lastIndexOf(",") + lastPointerPosition;
						place = fileBody.substring(lastPointerPosition, placeEndPosition);

						/* Remove any new line characters */
						if (place != null) {
							place = place.replaceAll("[\\n\\r]+", " ");
						}
						
						placeCount++;
					}
					lastPointerPosition = dateEndPosition;
				}
			}

			/* Content */
			if (fileBody.substring(lastPointerPosition) != null) {
				if (fileBody.substring(lastPointerPosition).trim() != null && !fileBody.substring(lastPointerPosition).isEmpty()) {
					content = fileBody.substring(lastPointerPosition).trim();
					content = (content.charAt(0) == '-') ? content.substring(1).trim() : content;
				}
				
				/* Remove any new line characters */
				if (content != null) {
					content = content.replaceAll("[\\n\\r]+", " ");
				}
				
				contentCount++;
			}

			if (fileId.equals("") || fileId.equals("")) {
				System.out.println("\nFile ID: " + fileId);
				System.out.println("Category: " + category);
				System.out.println("Title: " + title);
				System.out.println("Author: " + author);
				System.out.println("Author org: " + authorOrg);
				System.out.println("Date: " + newsDate);
				System.out.println("Place: " + place);
				System.out.println("Content: " + content);
			}
			if (fileId != null)
				document.setField(FieldNames.FILEID, fileId);
			if (category != null)
				document.setField(FieldNames.CATEGORY, category);
			if (title != null)
				document.setField(FieldNames.TITLE, title);
			if (author != null)
				document.setField(FieldNames.AUTHOR, author);
			if (authorOrg != null)
				document.setField(FieldNames.AUTHORORG, authorOrg);
			if (place != null)
				document.setField(FieldNames.PLACE, place);
			if (newsDate != null)
				document.setField(FieldNames.NEWSDATE, newsDate);
			if (content != null)
				document.setField(FieldNames.CONTENT, content);
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			errorCount++;
		}

		return document;
	}
}