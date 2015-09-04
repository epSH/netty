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
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public class SmtpRequestEncoder extends MessageToMessageEncoder<Object> {
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] DOT_CRLF = {'.', '\r', '\n'};
    private static final byte SP = ' ';
    private static final ByteBuf DOT_CRLF_BUFFER = Unpooled.unreleasableBuffer(
            Unpooled.directBuffer(3).writeBytes(DOT_CRLF));

    private boolean contentExpected;

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg instanceof SmtpRequest || msg instanceof SmtpContent;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg instanceof SmtpRequest) {
            if (contentExpected) {
                throw new IllegalStateException("SmtpContent expected");
            }
            boolean release = true;
            ByteBuf buffer = ctx.alloc().buffer();
            try {
                SmtpRequest req = (SmtpRequest) msg;
                req.cmd().encode(buffer);
                writeParameters(req.parameters(), buffer);
                buffer.writeBytes(CRLF);
                out.add(buffer);
                release = false;
                if (req.cmd().isContentExpected()) {
                    contentExpected = true;
                }
            } finally {
                if (release) {
                    buffer.release();
                }
            }
        }

        if (msg instanceof SmtpContent) {
            if (!contentExpected) {
                throw new IllegalStateException("No SmtpContent expected");
            }
            ByteBuf content = ((SmtpContent) msg).content();
            out.add(content.retain());
            if (msg instanceof LastSmtpContent) {
                out.add(DOT_CRLF_BUFFER.duplicate().retain());
                contentExpected = false;
            }
        }
    }

    private static void writeParameters(List<CharSequence> parameters, ByteBuf out) {
        if (parameters.isEmpty()) {
            return;
        }
        out.writeByte(SP);
        if (parameters instanceof RandomAccess) {
            int sizeMinusOne = parameters.size() - 1;
            for (int i = 0; i < sizeMinusOne; i++) {
                ByteBufUtil.writeAscii(out, parameters.get(i));
                out.writeByte(SP);
            }
            ByteBufUtil.writeAscii(out, parameters.get(sizeMinusOne));
        } else {
            Iterator<CharSequence> params = parameters.iterator();
            for (;;) {
                ByteBufUtil.writeAscii(out, params.next());
                if (params.hasNext()) {
                    out.writeByte(SP);
                } else {
                    break;
                }
            }
        }
    }
}
