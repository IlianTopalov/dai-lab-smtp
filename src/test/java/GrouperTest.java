import ch.heig.dai.lab.smtp.Grouper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GrouperTest {
	@Test
	public void groupLinesTest() {
		List<String> list = List.of(
			"Paul",
			"Pierre",
			"Jean-Luc",
			"Artur",
			"Ilian",
			"Fabrice",
			"Nicolas",
			"Pedro",
			"Samih",
			"Leo"
		);

		List<List<String>> expected = List.of(
			List.of("Paul", "Pierre", "Jean-Luc", "Artur"),
			List.of("Ilian", "Fabrice", "Nicolas", "Pedro"),
			List.of("Samih", "Leo")
		);

		var groupedLines = Grouper.groupLines(list, 3);
		Assertions.assertEquals(expected, groupedLines);
	}
}
