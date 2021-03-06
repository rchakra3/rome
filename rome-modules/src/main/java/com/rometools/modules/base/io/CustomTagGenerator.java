/*
 * CustomTagGenerator.java
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License or the Apache
 * License at your discretion.
 *
 *  Copyright (C) 2006  Robert Cooper, Temple of the Screaming Penguin
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rometools.modules.base.io;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom2.Element;
import org.jdom2.Namespace;

import com.rometools.modules.base.CustomTag;
import com.rometools.modules.base.CustomTagImpl;
import com.rometools.modules.base.CustomTags;
import com.rometools.modules.base.types.DateTimeRange;
import com.rometools.modules.base.types.FloatUnit;
import com.rometools.modules.base.types.IntUnit;
import com.rometools.modules.base.types.ShortDate;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.io.ModuleGenerator;

/**
 * @version $Revision: 1.1 $
 * @author <a href="mailto:cooper@screaming-penguin.com">Robert "kebernet" Cooper</a>
 */
public class CustomTagGenerator implements ModuleGenerator {

    static final HashSet<Namespace> NAMESPACES = new HashSet<Namespace>();

    static {
        NAMESPACES.add(CustomTagParser.NS);
    }

    /** Creates a new instance of CustomTagGenerator */
    public CustomTagGenerator() {
    }

    @Override
    public String getNamespaceUri() {
        return CustomTags.URI;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    @Override
    public void generate(final Module module, final Element element) {
        if (!(module instanceof CustomTags)) {
            return;
        }

        final List<CustomTag> tags = ((CustomTags) module).getValues();
        final Iterator<CustomTag> it = tags.iterator();

        while (it.hasNext()) {
            final CustomTag tag = it.next();

            if (tag.getValue() instanceof DateTimeRange) {
                final DateTimeRange dtr = (DateTimeRange) tag.getValue();
                final Element newTag = new Element(tag.getName(), CustomTagParser.NS);
                newTag.setAttribute("type", "dateTimeRange");
                newTag.addContent(generateSimpleElement("start", GoogleBaseParser.LONG_DT_FMT.format(dtr.getStart())));
                newTag.addContent(generateSimpleElement("end", GoogleBaseParser.LONG_DT_FMT.format(dtr.getEnd())));
                element.addContent(newTag);
            } else if (tag.getValue() instanceof ShortDate) {
                final ShortDate sd = (ShortDate) tag.getValue();
                final Element newTag = generateSimpleElement(tag.getName(), GoogleBaseParser.SHORT_DT_FMT.format(sd));
                newTag.setAttribute("type", "date");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof Date) {
                final Date d = (Date) tag.getValue();
                final Element newTag = generateSimpleElement(tag.getName(), GoogleBaseParser.SHORT_DT_FMT.format(d));
                newTag.setAttribute("type", "dateTime");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof Integer) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "int");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof IntUnit) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "intUnit");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof Float) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "float");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof FloatUnit) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "floatUnit");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof String) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "string");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof URL) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "url");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof Boolean) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "boolean");
                element.addContent(newTag);
            } else if (tag.getValue() instanceof CustomTagImpl.Location) {
                final Element newTag = generateSimpleElement(tag.getName(), tag.getValue().toString());
                newTag.setAttribute("type", "location");
                element.addContent(newTag);
            }
        }
    }

    protected Element generateSimpleElement(final String name, final String value) {
        final Element element = new Element(name, CustomTagParser.NS);
        element.addContent(value);

        return element;
    }
}
