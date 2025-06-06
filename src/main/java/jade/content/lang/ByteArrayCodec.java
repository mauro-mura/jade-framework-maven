/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.content.lang;

import jade.content.onto.*;
import jade.content.abs.*;

/**
 * Base class for content language codecs that transform 
 * AbsContentElements to/from sequences of bytes
 * @author Giovanni Caire - TILAB
 */
public abstract class ByteArrayCodec extends Codec{

    /**
     * Construct a ByteArrayCodec object with the given name
     */
    protected ByteArrayCodec(String name) {
    	super(name);
    }

    /**
     * Encodes a content into a byte array.
     * @param content the content as an abstract descriptor.
     * @return the content as a byte array.
     * @throws CodecException
     */
    public abstract byte[] encode(AbsContentElement content) 
            throws CodecException;

    /**
     * Encodes a content into a byte array.
     * @param ontology the ontology 
     * @param content the content as an abstract descriptor.
     * @return the content as a byte array.
     * @throws CodecException
     */
    public abstract byte[] encode(Ontology ontology, AbsContentElement content) 
            throws CodecException;

    /**
     * Decodes the content to an abstract description.
     * @param content the content as a byte array.
     * @return the content as an abstract description.
     * @throws CodecException
     */
    public abstract AbsContentElement decode(byte[] content) 
            throws CodecException;

    /**
     * Decodes the content to an abstract description.
     * @param ontology the ontology.
     * @param content the content as a byte array.
     * @return the content as an abstract description.
     * @throws CodecException
     */
    public abstract AbsContentElement decode(Ontology ontology, byte[] content) 
            throws CodecException;

}

