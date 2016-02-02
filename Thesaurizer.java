import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Thesaurizer
{
	private static String SYNONYMS_TOKEN = "<h2>\nSynonyms";
	private static String ANTONYMS_TOKEN = "<h2>Antonyms";
	private static String TAG_START = "<span class=\"text\">";
	private static String TAG_END = "</span>";
	private static String URL_START = "http://www.thesaurus.com/browse/";

	private static String PUNCTUATION_REGEX = "\\p{Punct}";
	private static String CAPITAL_REGEX = "[A-Z]";

	public static void main(String[] args)
	{
		String string = getFile(args[0]);
		System.out.println(string);
		System.out.println(thesaurizeString(string));
	}

	private static String thesaurizeString(String input)
	{
		String[] words = input.split(" ");
		for (int i = 0; i < words.length; i++)
		{
			String word = words[i];

			int j = word.length() -1;
			StringBuilder builder = new StringBuilder();
			while (j > -1)
			{
				String charAt = Character.toString(word.charAt(j));
				if (charAt.matches(PUNCTUATION_REGEX))
				{
					j--;
					builder.append(charAt);
				}
				else
				{
					break;
				}
			}
			String punc = builder.reverse().toString();

			ArrayList<String> synonyms = getSynonyms(word.substring(0, j + 1));
			if (synonyms.size() != 0)
			{
				String synonym = synonyms.get((int) (Math.random() * (synonyms.size() - 1)));
				if (Character.toString(word.charAt(0)).matches(CAPITAL_REGEX))
				{
					synonym = synonym.substring(0, 1).toUpperCase() + synonym.substring(1);
				}
				synonym = matchTense(synonym, word);
				words[i] = synonym + punc;
			}
		}

		return buildString(words);
	}

	private static String matchTense(String synonym, String original)
	{
		int originalLen = original.length();

		if (originalLen > 3)
		{
			int synonymLen = synonym.length();

			if (original.substring(originalLen - 2).equals("ed") && 
				!synonym.substring(synonymLen - 2).equals("ed"))
			{
				if (synonym.substring(synonymLen - 1).equals("e"))
				{
					return synonym + "d";
				}
				else
				{
					return synonym + "ed";
				}
			}
			else if (original.substring(originalLen - 3).equals("ing") && 
					 !synonym.substring(synonymLen - 3).equals("ing"))
			{
				return synonym + "ing";
			}
			else
			{
				return synonym;
			}
		}
		else
		{
			return synonym;
		}
	}

	private static String buildString(String[] array)
	{
		StringBuilder builder = new StringBuilder();
		for (String string : array)
		{
			if (builder.length() > 0)
			{
				builder.append(" ");
			}

			builder.append(string);
		}

		return builder.toString();
	}

	private static ArrayList<String> getSynonyms(String word)
	{
		String html = getHTML(URL_START + word);
		ArrayList<String> matches = new ArrayList<String>();

		int i = html.indexOf(SYNONYMS_TOKEN);
		int end = html.indexOf(ANTONYMS_TOKEN);
		while (i < end)
		{
			int index = html.indexOf(TAG_START, i);
			if (index < end && index != -1)
			{
				int wordStart = index + TAG_START.length();
				int wordEnd = html.indexOf(TAG_END, wordStart);
				matches.add(html.substring(wordStart, wordEnd));
				i = wordEnd + TAG_END.length();
			}
			else
			{
				i++;
			}
		}

		return matches;
	}

	private static String getHTML(String address)
	{
		URL url;
		try
		{
			url = new URL(address);
		}
		catch (MalformedURLException ex)
		{
			return "";
		}

		BufferedReader in = null;

		try
		{
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder builder = new StringBuilder();

			String input = null;
			do
			{
				input = in.readLine();
				if (input != null)
				{
					builder.append(input);
				}
			}
			while (input != null);

			return builder.toString();
		}
		catch (IOException ex)
		{

		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		return "";
	}

	private static String getFile(String path)
	{
		FileInputStream fileIn = null;

		int fileLength = (int) new File(path).length();
		char[] file = new char[fileLength];

		try
		{
			fileIn = new FileInputStream(path);

			for(int i = 0; i < fileLength; i++)
			{
				file[i] = (char) fileIn.read();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if (fileIn != null)
				{
					fileIn.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

		return new String(file);
	}
}