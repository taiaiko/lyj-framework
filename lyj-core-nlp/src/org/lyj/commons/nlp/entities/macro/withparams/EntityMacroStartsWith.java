package org.lyj.commons.nlp.entities.macro.withparams;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Rule:
 * - Starts with x
 *
 * <p>
 * Usage:
 * #startsWithExp#VEN-
 */
public class EntityMacroStartsWith
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "startsWith"; //

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _start_text;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroStartsWith(final String[] args) {
        super(NAME, args);
        _start_text = args.length > 0 ? args[0] : "";
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public String[] parse(final String lang,
                          final int start_index,
                          final String[] phrase) {
        final Collection<String> result = new LinkedList<>();
        for (int i = start_index; i < phrase.length; i++) {
            final String word = phrase[i];
            if (StringUtils.hasText(_start_text) && word.startsWith(_start_text)) {
                result.add(word);
            }
        }
        return result.toArray(new String[0]);
    }


}
