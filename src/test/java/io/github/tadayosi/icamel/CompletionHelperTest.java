package io.github.tadayosi.icamel;

import org.eclipse.lsp4j.Position;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CompletionHelperTest {

    @Test
    public void testPosition() {
        String code = "// camel-k\n" +
            "from('timer:js?period=3000')\n" +
            "    .log('hello!')";
        int at = 17;
        assertThat(CompletionHelper.position(code, at)).isEqualTo(new Position(1, 6));
    }

    @Test
    public void testAt() {
        String code = "// camel-k\n" +
            "from('timer:js?period=3000')\n" +
            "    .log('hello!')";
        Position position = new Position(1, 6);
        assertThat(CompletionHelper.at(code, position)).isEqualTo(17);
    }
}
