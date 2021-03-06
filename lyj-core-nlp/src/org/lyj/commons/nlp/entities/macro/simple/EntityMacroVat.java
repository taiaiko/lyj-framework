package org.lyj.commons.nlp.entities.macro.simple;

import org.lyj.commons.nlp.entities.macro.AbstractEntityMacro;
import org.lyj.commons.util.RegExpUtils;

import java.util.Collection;
import java.util.LinkedList;

public class EntityMacroVat
        extends AbstractEntityMacro {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    public static final String NAME = "vat";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public EntityMacroVat() {
        super(NAME);
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
            if (RegExpUtils.isValidVatNumber(word)) {
                result.add(word);
            }
        }

        return result.toArray(new String[0]);
    }


}
