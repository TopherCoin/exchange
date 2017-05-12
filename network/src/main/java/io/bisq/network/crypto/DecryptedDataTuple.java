/*
 * This file is part of bisq.
 *
 * bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bisq.network.crypto;


import io.bisq.common.network.NetworkEnvelope;
import lombok.EqualsAndHashCode;

import java.security.PublicKey;

@EqualsAndHashCode
public final class DecryptedDataTuple {
    public final NetworkEnvelope payload;
    public final PublicKey sigPublicKey;

    public DecryptedDataTuple(NetworkEnvelope payload, PublicKey sigPublicKey) {
        this.payload = payload;
        this.sigPublicKey = sigPublicKey;
    }

    @Override
    public String toString() {
        return "DecryptedPayloadWithPubKey{" +
                "payload=" + payload +
                ", sigPublicKey.hashCode()=" + sigPublicKey.hashCode() +
                '}';
    }
}
