package org.lyj.commons.nlp.entities.macro.withparams;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

public class EntityMacroContains
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "contains"; // detects names of persons (first should be Uppercase)

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _text;

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroContains(final String[] args) {
        super(NAME, args);
        _text = args.length > 0 ? args[0] : "";
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
            if (StringUtils.hasText(_text) && word.contains(_text)) {
                result.add(word);
            }
        }
        return result.toArray(new String[0]);
    }


}