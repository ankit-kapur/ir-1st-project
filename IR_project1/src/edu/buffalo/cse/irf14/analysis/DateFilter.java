package edu.buffalo.cse.irf14.analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFilter extends TokenFilter {

//	public static float dateTime;
	
	TokenStream tokenStream;

	List<String> datePatternString = new ArrayList<String>();
	Pattern datePattern0;
	Pattern datePattern1;
	Pattern datePattern2;
	Pattern datePattern3;
	Pattern datePattern4;
	Pattern datePattern5;
	Pattern datePattern6;
	Pattern datePattern7;
	Pattern datePattern8;

	// List<TokenTracker> listOfTokenTrackers;
	String tokenStreamString = "";

	public DateFilter(TokenStream stream) {
		super(stream);
		this.tokenStream = stream;
		addPatterns();
	}

	@Override
	public boolean increment() throws TokenizerException {
		if (tokenStream != null && tokenStream.hasNext()) {
			Token token = tokenStream.next();
			if (token != null) {
				tokenStreamString += token.getTermText() + " ";
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TokenStream getStream() {
		TokenStream modifiedStream = null;
		try {
			modifiedStream = processFilterForToken();
		} catch (FilterException e) {
			System.err.println(e.getMessage());
		}
		return modifiedStream;
	}

	@SuppressWarnings("deprecation")
	public TokenStream processFilterForToken() throws FilterException {

		long startTime = new Date().getTime();
		TokenStream modifiedTokenStream = null;
		tokenStreamString = tokenStreamString.trim();
		String dateFound = null;
		boolean twoTokensCaseOccured = false;

		StringBuffer changedString = new StringBuffer(tokenStreamString);
		int offset = 0;

		/* 00:58:53UTC on Sunday, 26 December 2004 ---> 20041226 00:58:53 */
		Matcher matcher = datePattern0.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			boolean validDate = false;
			String newDateString = null;
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());

			String time = matcher.group(1);
			String dateString = matcher.group(14);
			int year = (matcher.group(29) == null) ? 1900 : Integer.parseInt(matcher.group(29));

			/* Validate the date and time */
			SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
			df.setLenient(false);
			String testDateString = dateString + " " + time;
			testDateString = testDateString.replaceAll("[\\n\\r\\t]+", " ").replaceAll("Sept(?!(ember))", "Sep");
			try {
				df.parse(testDateString);
				validDate = true;
			} catch (ParseException e) {
				df = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
				df.setLenient(false);
				try {
					df.parse(testDateString);
					validDate = true;
				} catch (ParseException e2) {
					// System.out.println("INVALID date: " +
					// testDateString);
					continue;
				}
			}

			if (validDate) {
				Date date = new Date(testDateString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				String yearString = String.format("%04d", year);
				String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
				String dayOfMonth = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
				String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
				String seconds = String.format("%02d", calendar.get(Calendar.SECOND));

				newDateString = yearString + month + dayOfMonth + "@" + hour + ":" + minute + ":" + seconds;

				twoTokensCaseOccured = true;
				changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), newDateString);
			}
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 1 January 1978 --> 19780101 */
		matcher = datePattern1.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			boolean validDate = false;
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());
			dateFound = dateFound.replaceAll("[\\n\\r\\t]+", " ").replaceAll("Sept(?!(ember))", "Sep");

			SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
			df.setLenient(false);

			String newDateString = null;
			String testDateString = dateFound;
			int year = (matcher.group(15) == null) ? 1900 : Integer.parseInt(matcher.group(15));

			try {
				df.parse(dateFound);
				validDate = true;
			} catch (ParseException e) {
				df = new SimpleDateFormat("dd MMMM, yyyy");
				df.setLenient(false);
				try {
					df.parse(dateFound);
					validDate = true;
				} catch (ParseException e2) {
					// System.out.println("INVALID date: " + dateFound);
					continue;
				}
			}

			if (validDate) {
				Date date = new Date(testDateString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				String yearString = String.format("%04d", year);
				String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
				String dayOfMonth = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

				newDateString = yearString + month + dayOfMonth;

				changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), newDateString);
			}
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* December 7, 1941 --> 19411207 */
		matcher = datePattern2.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			boolean validDate = false;
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());
			dateFound = dateFound.replaceAll("[\\n\\r\\t]+", " ").replaceAll("Sept(?!(ember))", "Sep");
			;

			String newDateString = null;
			String testDateString = dateFound;

			int year = (matcher.group(15) == null) ? 1900 : Integer.parseInt(matcher.group(15));

			SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
			df.setLenient(false);
			try {
				df.parse(dateFound);
				validDate = true;
			} catch (ParseException e1) {
				df = new SimpleDateFormat("MMMM dd yyyy");
				df.setLenient(false);
				try {
					df.parse(dateFound);
					validDate = true;
				} catch (ParseException e2) {
					// System.out.println("INVALID date: " + dateFound);
					continue;
				}
			}

			if (validDate) {
				Date date = new Date(testDateString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				String yearString = String.format("%04d", year);
				String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
				String dayOfMonth = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

				newDateString = yearString + month + dayOfMonth;

				changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), newDateString);
			}
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 84 BC --> -00840101 */
		matcher = datePattern3.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());

			int year = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
			String yearString = String.format("%04d", year);
			changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), "-" + yearString + "0101");
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 847 AD --> 08470101 */
		matcher = datePattern4.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());

			int year = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
			String yearString = String.format("%04d", year);
			changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), yearString + "0101");
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 2011-12 --> 20110101-20120101 */
		matcher = datePattern5.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());

			String yearString1 = matcher.group(1);
			String yearString2 = yearString1.substring(0, 2) + matcher.group(3);

			changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), yearString1 + "0101-" + yearString2 + "0101");
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 5:15PM --> 17:15:00 */
		matcher = datePattern6.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());
			dateFound = dateFound.replaceAll("[\\n\\r\\t]+", " ").replaceAll("Sept(?!(ember))", "Sep");

			SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
			df.setLenient(false);
			boolean validDate = false;

			String timeString = matcher.group(1);
			String timeAmPm = matcher.group(5);
			try {
				df.parse(dateFound);
				validDate = true;
			} catch (ParseException e) {
				df = new SimpleDateFormat("hh:mmaa");
				df.setLenient(false);
				try {
					df.parse(dateFound);
					validDate = true;
				} catch (ParseException e2) {
					// System.out.println("INVALID date: " + dateFound);
					continue;
				}
			}

			if (validDate) {
				String testDateString = "01/01/1900 " + timeString + " " + timeAmPm;
				Date date = new Date(testDateString);
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);

				String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
				String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
				String seconds = String.format("%02d", calendar.get(Calendar.SECOND));

				changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), (hour + ":" + minute + ":" + seconds));
			}
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* April 11 --> 19000411 */
		matcher = datePattern7.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());
			dateFound = dateFound.replaceAll("[\\n\\r\\t]+", " ").replaceAll("Sept(?!(ember))", "Sep");
			/* Check if day of the month is valid */
			String dayOfTheMonth = dateFound.substring(dateFound.indexOf(' ') + 1);
			if (!(dayOfTheMonth != null && !dayOfTheMonth.trim().isEmpty() && Integer.parseInt(dayOfTheMonth) > 0 && Integer.parseInt(dayOfTheMonth) <= 31)) {

				// System.out.println("INVALIDDDD date: " + dateFound);
				continue;
			}

			Date date = new Date(dateFound + " 1900");
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);

			String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
			String dayOfMonth = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

			changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), (1900 + month + dayOfMonth));
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* 1948 --> 19480101 */
		matcher = datePattern8.matcher(tokenStreamString);
		while (matcher.find()) {
			offset = changedString.length() - tokenStreamString.length();
			dateFound = tokenStreamString.substring(matcher.start(), matcher.end());

			changedString = changedString.replace(offset + matcher.start(), offset + matcher.end(), dateFound + "0101");
		}
		tokenStreamString = changedString.toString();
		changedString = new StringBuffer(tokenStreamString);

		/* Make a new stream with the modified string, and return it */
		try {
			if (tokenStreamString != null && !tokenStreamString.trim().isEmpty()) {
				modifiedTokenStream = new Tokenizer().consume(tokenStreamString);
			}
			/*
			 * For the special case (1) where 20041226 00:58:53 is required
			 * to be a single token
			 */
			if (twoTokensCaseOccured) {
				while (modifiedTokenStream.hasNext()) {
					Token currentToken = modifiedTokenStream.next();
					String currentTokenText = currentToken.getTermText();
					Pattern twoTokensCasePattern = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d@\\d\\d:\\d\\d:\\d\\d");
					Matcher twoTokensCaseMatcher = twoTokensCasePattern.matcher(currentTokenText);
					if (twoTokensCaseMatcher.find()) {
						currentToken.setTermText(currentTokenText.replace('@', ' '));
					}
				}
			}
		} catch (TokenizerException e) {
			throw new FilterException("A Tokenizer exception occured in the consume method, while tokenizing the string: " + tokenStreamString);
		}

//		dateTime += (new Date().getTime() - startTime) / 1000.0;
		return modifiedTokenStream;
	}

	private void addPatterns() {

		/* 00:58:53UTC on Sunday, 26 December 2004 ---> 20041226 00:58:53 */
		datePatternString.add("(\\d\\d:\\d\\d:\\d\\d)(ACDT|ACST|ADT|ADT|AEDT|AEDT|AEST|AFT|AKDT|AKST|ALMT|AMST|AMST|AMT|AMT|ANAST|ANAT|AQTT|ART|AST|AST|AST|AST|AWDT|AWST|AZOST|AZOT|AZST|AZT|BNT|BOT|BRST|BRT|BST|BST|BTT|CAST|CAT|CCT|CDT|CDT|CEST|CET|CET|CHADT|CHAST|CKT|CLST|CLT|COT|CST|CST|CST|CST|CVT|CXT|ChST|DAVT|EASST|EAST|EAT|EAT|ECT|EDT|EDT|EEST|EEST|EEST|EET|EET|EET|EGST|EGT|EST|EST|EST|ET|ET|ET|FJST|FJT|FKST|FKT|FNT|GALT|GAMT|GET|GFT|GILT|GMT|GST|GYT|HAA|HAA|HAC|HADT|HAE|HAE|HAP|HAR|HAST|HAT|HAY|HKT|HLV|HNA|HNA|HNA|HNC|HNC|HNE|HNE|HNE|HNP|HNR|HNT|HNY|HOVT|ICT|IDT|IOT|IRDT|IRKST|IRKT|IRST|IST|IST|IST|JST|KGT|KRAST|KRAT|KST|KUYT|LHDT|LHST|LINT|MAGST|MAGT|MART|MAWT|MDT|MESZ|MEZ|MHT|MMT|MSD|MSK|MST|MUT|MVT|MYT|NCT|NDT|NFT|NOVST|NOVT|NPT|NST|NUT|NZDT|NZDT|NZST|NZST|OMSST|OMST|PDT|PET|PETST|PETT|PGT|PHOT|PHT|PKT|PMDT|PMST|PONT|PST|PST|PT|PWT|PYST|PYT|RET|SAMT|SAST|SBT|SCT|SGT|SRT|SST|TAHT|TFT|TJT|TKT|TLT|TMT|TVT|ULAT|UTC|UYST|UYT|UZT|VET|VLAST|VLAT|VUT|WAST|WAT|WEST|WEST|WESZ|WET|WET|WEZ|WFT|WGST|WGT|WIB|WIT|WITA|WST|WST|WT|YAKST|YAKT|YAPT|YEKST|YEKT)? [Oo][Nn] ([Ss]un(day)?|[Mm]on(day)?|[Tt]ue(s)?(day)?|[Ww]ed(nesday)?|[Tt]hur(s)?(day)?|[Ff]ri(day)?|[Ss]at(urday)?)(,)? ((\\d)?\\d[\\s]*([Jj]an(uary)?|[Ff]eb(ruary)?|[Mm]ar(ch)?|[Aa]pr(il)?|[Mm]ay|[Jj]un(e)?|[Jj]ul(y)?|[Aa]ug(ust)?|[Ss]ept(ember)?|[Oo]ct(ober)?|[Nn]ov(ember)?|[Dd]ec(ember)?)(,)?[\\s]*((?<![\\w\\d])(\\d)?(\\d)?(\\d)?\\d(?![\\w\\d])))");
		datePattern0 = Pattern.compile(datePatternString.get(0));

		/* 1 January 1978 --> 19780101 */
		datePatternString.add("(\\d)?\\d[\\s]*([Jj]an(uary)?|[Ff]eb(ruary)?|[Mm]ar(ch)?|[Aa]pr(il)?|[Mm]ay|[Jj]un(e)?|[Jj]ul(y)?|[Aa]ug(ust)?|[Ss]ept(ember)?|[Oo]ct(ober)?|[Nn]ov(ember)?|[Dd]ec(ember)?)(,)?[\\s]*((?<![\\w\\d])(\\d)?(\\d)?(\\d)?\\d(?![\\w\\d]))");
		datePattern1 = Pattern.compile(datePatternString.get(1));

		/* December 7, 1941 --> 19411207 */
		datePatternString.add("([Jj]an(uary)?|[Ff]eb(ruary)?|[Mm]ar(ch)?|[Aa]pr(il)?|[Mm]ay|[Jj]un(e)?|[Jj]ul(y)?|[Aa]ug(ust)?|[Ss]ept(ember)?|[Oo]ct(ober)?|[Nn]ov(ember)?|[Dd]ec(ember)?)[\\s]*(\\d)?\\d(,)?[\\s]*((?<![\\w\\d])(\\d)?(\\d)?(\\d)?\\d(?![\\w\\d]))");
		datePattern2 = Pattern.compile(datePatternString.get(2));

		/* 84 BC --> -00840101 */
		datePatternString.add("((\\d)?\\d)(\\s)?[bB][cC]");
		datePattern3 = Pattern.compile(datePatternString.get(3));

		/* 847 AD --> 08470101 */
		datePatternString.add("((?<![\\w\\d])(\\d)?(\\d)?(\\d)?\\d(?![\\d]))(\\s)?[aA][dD]");
		datePattern4 = Pattern.compile(datePatternString.get(4));

		/* 2011-12 --> 20110101-20120101 */
		datePatternString.add("((?<![\\d\\w])(\\d\\d\\d\\d)(?![\\w\\d]))-((?<![\\d\\w])(\\d\\d)(?![\\w\\d]))");
		datePattern5 = Pattern.compile(datePatternString.get(5));

		/* 5:15PM --> 17:15:00 */
		datePatternString.add("((\\d)?\\d:(\\d)?\\d)(\\s)?([pPaA][mM])");
		datePattern6 = Pattern.compile(datePatternString.get(6));

		/* April 11 --> 19000411 */
		datePatternString.add("([Jj]an(uary)?|[Ff]eb(ruary)?|[Mm]ar(ch)?|[Aa]pr(il)?|[Mm]ay|[Jj]un(e)?|[Jj]ul(y)?|[Aa]ug(ust)?|[Ss]ept(ember)?|[Oo]ct(ober)?|[Nn]ov(ember)?|[Dd]ec(ember)?)(\\s)?((\\d)?\\d(?!(\\d\\w)))");
		datePattern7 = Pattern.compile(datePatternString.get(7));

		/* 1948 --> 19480101 */
		datePatternString.add("(?<![\\d\\w])(\\d\\d\\d\\d)(?![\\w\\d-])");
		datePattern8 = Pattern.compile(datePatternString.get(8));
	}

	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub

	}
}