/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jpeek.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.BiFunc;
import org.cactoos.func.StickyBiFunc;
import org.cactoos.func.SyncBiFunc;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.http.Exit;
import org.takes.http.FtCli;
import org.takes.tk.TkWrap;

/**
 * Web application.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class TkApp extends TkWrap {

    /**
     * Ctor.
     * @param home Home directory
     */
    public TkApp(final Path home) {
        super(TkApp.make(home));
    }

    /**
     * Main Java entry point.
     * @param args Command line args
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        new FtCli(
            new TkApp(Files.createTempDirectory("jpeek")),
            args
        ).start(Exit.NEVER);
    }

    /**
     * Make it.
     * @param home Home directory
     * @return The take
     */
    private static Take make(final Path home) {
        final BiFunc<String, String, Path> reports = new StickyBiFunc<>(
            new SyncBiFunc<>(
                new Reports(home)
            )
        );
        return new TkForward(
            new TkFork(
                new FkRegex(
                    "/([^/]+)/([^/]+)(.*)",
                    new TkReport(reports)
                )
            )
        );
    }

}
