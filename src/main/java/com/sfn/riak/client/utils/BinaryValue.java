package com.sfn.riak.client.utils;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Lightweight utility class for byte arrays.
 * <p>
 * In almost all cases Riak is character asSet agnostic when it comes to data; buckets,
 * keys, values and metadata are all stored in Riak simply as bytes.
 * </p>
 * <p>
 * This utility class is used throughout the client in order to encapsulate
 * and allow access to those bytes by the user when the data is generally
 * representative of a {@code String} so that any character asSet may be used.
 * </p>
 * <br/><b>Thread Safety:</b><br/> If you are using this class directly keep in mind that any modification to the
 * wrapped <code>byte[]</code> post creation via either a retained reference to the
 * array after calling <code>unsafeCreate()</code> or accessing the array directly via
 * <code>unsafeGetValue()</code> will lead to undefined behavior in regard to thread
 * safety and visibility.
 * <p>
 *
 * @author Brian Roach <roach at basho dot com>
 * @since 2.0
 */
public final class BinaryValue
{
    /**
     * It is expected that UTF-8 charset is available.
     */
    private static final Charset theUTF8 = Charset.forName("UTF-8");

    private final byte[] data;

    private BinaryValue(final byte[] data)
    {
        this.data = data;
    }

    /**
     * Create a BinaryValue containing a copy of the supplied {@code byte[]}
     * <p>
     * A copy of the supplied {@code byte[]} is made.
     * </p>
     *
     * @param data the {@code byte[]} to copy
     * @return a new {@code BinaryValue}
     */
    @JsonCreator
    public static BinaryValue create(@JsonProperty("value") byte[] data)
    {
        if (data != null)
        {
           data = Arrays.copyOf(data, data.length);
        }
        return new BinaryValue(data);
    }

    /**
     * Create a BinaryValue containing a copy of the supplied String
     * encoded using the default character asSet.
     *
     * @param data the data to copy
     * @return a new {@code BinaryValue}
     */
    public static BinaryValue create(String data)
    {
        return create(data, DefaultCharset.get());
    }

    /**
     * Create a BinaryValue containing a copy of the supplied String
 encoded using UTF-8.
     *
     * @param data the data to copy
     * @return a new {@code BinaryValue}
     */
    public static BinaryValue createFromUtf8(String data)
    {
        return create(data, theUTF8);
    }

    /**
     * Create a BinaryValue containing a copy of the supplied string
 encoded using the supplied Charset.
     *
     * @param data the data to copy
     * @param charset the charset to use for encoding
     * @return a new {@code BinaryValue}
     */
    public static BinaryValue create(String data, Charset charset)
    {
        byte[] bytes = null;
        if (data != null)
        {
                bytes = data.getBytes(charset);
        }
        return new BinaryValue(bytes);
    }

    /**
     * Create a BinaryValue containing the supplied {@code byte[]}
     * <p>
     * The {@code byte[]} is not copied.
     * <p>
     * @param data the {@code byte[]} to wrap
     * @return a new {@code BinaryValue}
     * <br/><b>Thread Safety:</b><br/> The supplied <code>byte[]</code> is not copied and the reference is used
     * directly. Retaining a reference to this array and making subsequent
     * changes will lead to undefined behavior in regard to thread safety and
     * visibility.
     */
    public static BinaryValue unsafeCreate(byte[] data)
    {
        return new BinaryValue(data);
    }

    /**
     * Return a copy of the wrapped {@code byte[]}
     * <p>
     * {@link Arrays#copyOf(byte[], int) } is used to copy the internal {@code byte[]}
     * </p>
     * @return a copy of the internal {@code byte[]}
     */
    public byte[] getValue()
    {
        if (data != null)
        {
            return Arrays.copyOf(data, data.length);
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the wrapped {@code byte[]}
     * @return the internal {@code byte[]}
     * <br/><b>Thread Safety:</b><br/> This method exposes the internal <code>byte[]</code> directly.
     * Modifying the contents of this array will lead to undefined behavior in
     * regard to thread safety and visibility.
     */
    public byte[] unsafeGetValue()
    {
        return data;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof BinaryValue))
        {
            return false;
        }
        else
        {
            return Arrays.equals(data, ((BinaryValue)other).data);
        }
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }

    /**
     * Return the wrapped {@code byte[]} as a String.
     * <p>
     * The default character asSet is used for encoding.
     * </p>
     * @return a {@code String} created using the default {@code Charset}
     */
    @Override
    public String toString()
    {
        return toString(DefaultCharset.get());
    }

    /**
     * Return the wrapped {@code byte[]} as a String.
     * <p>
     * UTF-8 is used for encoding.
     * </p>
     * @return a {@code String} created using the UTF-8 {@code Charset}
     */
    public String toStringUtf8()
    {
        return toString(theUTF8);
    }

    /**
     * Return the wrapped {@code byte[]} as a String
     * <p>
     * The supplied {@code Charset} is used to convert the bytes.
     * @return a {@code String} created using the supplied {@code Charset}
     */
    public String toString(Charset charset)
    {
        if (data != null)
        {
            return new String(data, charset);
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the length of the wrapped {@code byte[]}
     * @return the length.
     */
    public int length()
    {
        return data.length;
    }
}
