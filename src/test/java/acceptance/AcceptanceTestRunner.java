package acceptance;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:huntthewumpus.feature"},
        plugin = {"pretty"},
        snippets = CucumberOptions.SnippetType.CAMELCASE)
public class RunAcceptanceTests {
}


