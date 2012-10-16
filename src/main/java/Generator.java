import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Joiner;

public class Generator {

	private Random rand = new Random();
	private DateTime dt = new DateTime(-12599128349000l);

	public interface BufferedReaderVistor {

		void doFile(int fileNum, BufferedReader reader) throws IOException;

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Generator gen = new Generator();
		gen.generate();
	}

	private void generate() throws IOException {
		final Set<String> badNames = new HashSet<String>();
		badNames.add("Nob");
		badNames.add("Sh");
		badNames.add("Am");
		badNames.add("An");
		badNames.add("And");
		badNames.add("Noble");
		badNames.add("This");
		badNames.add("Shall");
		badNames.add("Mountaine");
		badNames.add("Plaines");
		badNames.add("Songs");
		badNames.add("Princesse");
		badNames.add("England");
		badNames.add("Stand");
		badNames.add("These");
		badNames.add("Mercy");
		badNames.add("Banquet");
		badNames.add("Wife");
		badNames.add("Conscience");
		badNames.add("Christening");
		badNames.add("Keepe");
		badNames.add("Doctor");
		badNames.add("Queene");
		final Pattern namePattern = Pattern
				.compile(" +([A-Z][A-Za-z.]+)\\. (.*)");
		final Set<String> names = new HashSet<String>();
		final Map<String, String> nameSubstitutions = new HashMap<String, String>();
		// populate the set of names from the stage directions,
		// and also guess at a map of stage direction names to full names
		doEachFile(new BufferedReaderVistor() {
			@Override
			public void doFile(int fileNum, BufferedReader reader)
					throws IOException {
				String line = null;
				while ((line = reader.readLine()) != null) {
					Matcher nameMatcher = namePattern.matcher(line);
					String other;
					if (nameMatcher.matches()) {
						String name = nameMatcher.group(1);
						if (!badNames.contains(name)) {
							names.add(name);
							nameSubstitutions.put(name, name);
						}
						other = nameMatcher.group(2);
					} else {
						other = line;
					}
					other = other.replaceAll("[^a-zA-Z ]", " ");
					String[] parts = other.split("\\s+");
					for (String name : names) {
						if (name.length() < 4 || badNames.contains(name))
							continue;
						for (String part : parts) {
							if (part.startsWith(name)) {
								nameSubstitutions.put(name, part.trim());
							}
						}
					}

				}
			}
		});
		doEachFile(new BufferedReaderVistor() {

			@Override
			public void doFile(int fileNum, BufferedReader reader)
					throws IOException {
				String line = null;
				Joiner joiner = Joiner.on(" ");
				int lineNum = 0;

				while ((line = reader.readLine()) != null) {
					lineNum++;
					StringBuilder currentTextBlockBuilder = new StringBuilder();
					boolean go;
					do {
						lineNum++;
						currentTextBlockBuilder.append(line);
						currentTextBlockBuilder.append(" ");
						line = reader.readLine();
						go = line != null && !line.equals("");
					} while (go);
					String currentTextBlock = currentTextBlockBuilder
							.toString();
					Matcher nameMatcher = namePattern.matcher(currentTextBlock);
					if (nameMatcher.matches()) {
						String name = nameSubstitutions.get(nameMatcher
								.group(1));
						String restOfText = nameMatcher.group(2);
						String[] tweetStrings = restOfText
								.split("!|\\.|\\?|:|;");
						int tweetNum = 0;
						for (String tweetString : tweetStrings) {
							tweetNum++;
							String[] parts = tweetString.split("\\s+");
							for (int i = 0; i < parts.length; i++) {
								if (!badNames.contains(parts[i])
										&& parts[i].length() > 3
										&& nameSubstitutions.values().contains(
												parts[i])) {
									parts[i] = "@" + parts[i];
								}
							}
							tweet(fileNum, lineNum, tweetNum, name,
									joiner.join(parts));
						}
					} else {
						continue;
					}
				}
			}

			private void tweet(int fileNum, int lineNum, int tweetNum,
					String name, String tweetText) {
				dt = dt.plus(rand.nextInt(80000));
				DateTimeFormatter dtf = DateTimeFormat.forPattern("EEE, ee MMM yyyy HH:mm:ss");
				// time needs to match Thu, 28 Jun 2012 19:16:21 +0000
				System.out.println(String.format(
						"{\"created_at\":\"%s +0000\",\"text\":\"%s\",\"from_user\":\"%s\"}",
						dtf.print(dt), tweetText.replaceAll("\"", "'"), name));
			}
		});
	}

	private void doEachFile(BufferedReaderVistor bufferedReaderVistor)
			throws IOException {
		for (int i = 1; i <= 42; i++) {
			BufferedReader reader = null;
			try {
				String resourceName = String.format(
						"/Shakespeare/0ws%02d10.txt", i);
				InputStream is = Generator.class
						.getResourceAsStream(resourceName);
				if (is == null) {
//					System.err.printf("File %s doesn't exist\n", resourceName);
					continue;
				}
				reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = reader.readLine()) != null
						&& !line.startsWith("David Reed")) {
					// skip all the lines that are notices from project
					// gutenberg
				}
				bufferedReaderVistor.doFile(i, reader);
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}
	}

}
