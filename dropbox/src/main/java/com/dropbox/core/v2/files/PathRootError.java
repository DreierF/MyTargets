/* DO NOT EDIT */
/* This file was generated from files.stone */

package com.dropbox.core.v2.files;

import com.dropbox.core.stone.StoneSerializers;
import com.dropbox.core.stone.StructSerializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.Arrays;

public class PathRootError {
    // struct files.PathRootError (files.stone)

    protected final String pathRoot;

    /**
     *
     * @param pathRoot  The user's latest path root value. None if the user no
     *     longer has a path root.
     */
    public PathRootError(String pathRoot) {
        this.pathRoot = pathRoot;
    }

    /**
     * None
     *
     * <p> The default values for unset fields will be used. </p>
     */
    public PathRootError() {
        this(null);
    }

    /**
     * The user's latest path root value. None if the user no longer has a path
     * root.
     *
     * @return value for this field, or {@code null} if not present.
     */
    public String getPathRoot() {
        return pathRoot;
    }

    @Override
    public int hashCode() {
        int hash = Arrays.hashCode(new Object [] {
            pathRoot
        });
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        // be careful with inheritance
        else if (obj.getClass().equals(this.getClass())) {
            PathRootError other = (PathRootError) obj;
            return (this.pathRoot == other.pathRoot) || (this.pathRoot != null && this.pathRoot.equals(other.pathRoot));
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Serializer.INSTANCE.serialize(this, false);
    }

    /**
     * Returns a String representation of this object formatted for easier
     * readability.
     *
     * <p> The returned String may contain newlines. </p>
     *
     * @return Formatted, multiline String representation of this object
     */
    public String toStringMultiline() {
        return Serializer.INSTANCE.serialize(this, true);
    }

    /**
     * For internal use only.
     */
    public static class Serializer extends StructSerializer<PathRootError> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(PathRootError value, JsonGenerator g, boolean collapse) throws IOException, JsonGenerationException {
            if (!collapse) {
                g.writeStartObject();
            }
            if (value.pathRoot != null) {
                g.writeFieldName("path_root");
                StoneSerializers.nullable(StoneSerializers.string()).serialize(value.pathRoot, g);
            }
            if (!collapse) {
                g.writeEndObject();
            }
        }

        @Override
        public PathRootError deserialize(JsonParser p, boolean collapsed) throws IOException, JsonParseException {
            PathRootError value;
            String tag = null;
            if (!collapsed) {
                expectStartObject(p);
                tag = readTag(p);
            }
            if (tag == null) {
                String f_pathRoot = null;
                while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
                    String field = p.getCurrentName();
                    p.nextToken();
                    if ("path_root".equals(field)) {
                        f_pathRoot = StoneSerializers.nullable(StoneSerializers.string()).deserialize(p);
                    }
                    else {
                        skipValue(p);
                    }
                }
                value = new PathRootError(f_pathRoot);
            }
            else {
                throw new JsonParseException(p, "No subtype found that matches tag: \"" + tag + "\"");
            }
            if (!collapsed) {
                expectEndObject(p);
            }
            return value;
        }
    }
}
