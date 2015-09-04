/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * The last part of a sequence of {@link SmtpContent}s that are send after a {@code DATA} request.
 * Be aware that a {@link SmtpContent} / {@link LastSmtpContent} sequence must always use CRLF as line delimiter
 * and that lines that start with a DOT must be escapted via an extra DOT as
 * specified by <a href="https://www.ietf.org/rfc/rfc2821.txt">RFC2821</a>.
 */
public interface LastSmtpContent extends SmtpContent {

    /**
     * Empty {@link LastSmtpContent}.
     */
    LastSmtpContent EMPTY_LAST_CONTENT = new LastSmtpContent() {
        @Override
        public LastSmtpContent copy() {
            return this;
        }

        @Override
        public LastSmtpContent duplicate() {
            return this;
        }

        @Override
        public LastSmtpContent retain() {
            return this;
        }

        @Override
        public LastSmtpContent retain(int increment) {
            return this;
        }

        @Override
        public LastSmtpContent touch() {
            return this;
        }

        @Override
        public LastSmtpContent touch(Object hint) {
            return this;
        }

        @Override
        public ByteBuf content() {
            return Unpooled.EMPTY_BUFFER;
        }

        @Override
        public int refCnt() {
            return 1;
        }

        @Override
        public boolean release() {
            return false;
        }

        @Override
        public boolean release(int decrement) {
            return false;
        }
    };

    @Override
    LastSmtpContent copy();

    @Override
    LastSmtpContent duplicate();

    @Override
    LastSmtpContent retain();

    @Override
    LastSmtpContent retain(int increment);

    @Override
    LastSmtpContent touch();

    @Override
    LastSmtpContent touch(Object hint);
}
