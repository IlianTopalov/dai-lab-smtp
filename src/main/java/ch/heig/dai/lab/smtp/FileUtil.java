package ch.heig.dai.lab.smtp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {
	private FileUtil() {}

	public static List<String> readList(String fileName) throws IOException {
		List<String> list = new LinkedList<>();

		// Open file, read and append each line to <list>
		BufferedReader reader = new BufferedReader(new InputStreamReader(
			new FileInputStream(fileName),
			StandardCharsets.UTF_8
		));

		String line;
		while ((line = reader.readLine()) != null)
			list.add(line);

		reader.close();

		return list;
	}

	public static <T> List<List<T>> groupLines(List<T> lines, int groupCount) {
		if (groupCount < 1) {
			throw new IllegalArgumentException("groupCount must be greater than 0.");
		}

		if (groupCount > lines.size()) {
			throw new IllegalArgumentException("groupCount must be less than the number of lines.");
		}

		int elementsPerGroup = lines.size() / groupCount + (lines.size() % groupCount == 0 ? 0 : 1);
		List<List<T>> result = new ArrayList<>(groupCount);
		for (int i = 0; i < groupCount; ++i) {
			result.add(new ArrayList<>(elementsPerGroup));
		}

		for (int i = 0; i < groupCount; ++i) {
			for (int j = 0; j < elementsPerGroup; ++j) {
				int sourceIdx = i * elementsPerGroup + j;
				if (sourceIdx >= lines.size()) {
					break;
				}

				result.get(i).add(lines.get(sourceIdx));
			}
		}

		return result;
	}
}
