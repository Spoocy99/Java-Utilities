package dev.spoocy.utils.reflection.scanner;

import java.lang.reflect.Modifier;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BasicScanner extends SingleClassScanner {

    @Override
    public boolean isVisible(int modifiers) {
        return Modifier.isPublic(modifiers);
    }
}
