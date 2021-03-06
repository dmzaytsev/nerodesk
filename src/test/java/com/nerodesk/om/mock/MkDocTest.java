/**
 * Copyright (c) 2015, nerodesk.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the nerodesk.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.nerodesk.om.mock;

import com.google.common.io.Files;
import com.nerodesk.om.Doc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link MkDoc}.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 * @since 0.3
 * @todo 95:30min method MkDoc.delete throws IOException if File.delete
 *  return false. Lets add unit test to check it. You need to find way
 *  to return false in File.delete (maybe make file Read Only via
 *  File.setReadOnly).
 */
public final class MkDocTest {
    /**
     * Temporary folder.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public final transient TemporaryFolder folder = new TemporaryFolder();

    /**
     * MkDoc exists can return false for missing file.
     * @throws IOException In case of error
     */
    @Test
    public void existsReturnsFalseForMissingFile() throws IOException {
        MatcherAssert.assertThat(
            new MkDoc(new File(this.folder.newFolder(), "foo"), "", "")
                .exists(),
            Matchers.is(false)
        );
    }

    /**
     * MkDoc exists can return true for existing file.
     * @throws IOException In case of error
     */
    @Test
    public void existsReturnsTrueForExistingFile() throws IOException {
        final File file = new File(this.folder.newFolder(), "bar");
        MatcherAssert.assertThat(
            file.createNewFile(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            new MkDoc(file, "", "").exists(),
            Matchers.is(true)
        );
    }

    /**
     * MkDoc can delete a file.
     * @throws IOException In case of error
     */
    @Test
    public void deletesFile() throws IOException {
        final File file = new File(this.folder.newFolder(), "test");
        MatcherAssert.assertThat(
            file.createNewFile(),
            Matchers.is(true)
        );
        new MkDoc(file, "", "").delete();
        MatcherAssert.assertThat(
            file.exists(),
            Matchers.is(false)
        );
    }

    /**
     * MkDoc can read from file.
     * @throws IOException In case of error
     */
    @Test
    public void readsFromFile() throws IOException {
        final File file = new File(this.folder.newFolder(), "readable");
        final String content = "read";
        Files.write(content.getBytes(StandardCharsets.UTF_8), file);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        new MkDoc(file, "", "").read(stream);
        MatcherAssert.assertThat(
            new String(stream.toByteArray(), StandardCharsets.UTF_8),
            Matchers.equalTo(content)
        );
    }

    /**
     * MkDoc can write to file.
     * @throws IOException In case of error
     */
    @Test
    public void writesToFile() throws IOException {
        final File file = new File(this.folder.newFolder(), "writable");
        final String content = "store";
        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        new MkDoc(file, "", "").write(
            new ByteArrayInputStream(bytes), bytes.length
        );
        MatcherAssert.assertThat(
            Files.toString(file, StandardCharsets.UTF_8),
            Matchers.equalTo(content)
        );
    }

    /**
     * MkDoc can retrieve file URL.
     * @throws IOException In case of error
     */
    @Test
    public void shortenUrl() throws IOException {
        final File file = new File(this.folder.newFolder(), "url");
        final String content = "shorten me";
        final Doc doc = new MkDoc(file, "", "");
        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        doc.write(
            new ByteArrayInputStream(bytes), bytes.length
        );
        MatcherAssert.assertThat(
            doc.shortUrl(),
            Matchers.equalTo(file.toURI().toURL().toString())
        );
    }
}
