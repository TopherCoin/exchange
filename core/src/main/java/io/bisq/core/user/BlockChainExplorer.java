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

package io.bisq.core.user;

import com.google.protobuf.Message;
import io.bisq.common.app.Version;
import io.bisq.common.persistable.PersistablePayload;
import io.bisq.generated.protobuffer.PB;

public final class BlockChainExplorer implements PersistablePayload {
    // That object is saved to disc. We need to take care of changes to not break deserialization.
    private static final long serialVersionUID = Version.LOCAL_DB_VERSION;

    public final String name;
    public final String txUrl;
    public final String addressUrl;

    public BlockChainExplorer(String name, String txUrl, String addressUrl) {
        this.name = name;
        this.txUrl = txUrl;
        this.addressUrl = addressUrl;
    }

    @Override
    public Message toProtoMessage() {
        return PB.BlockChainExplorer.newBuilder().setName(name).setTxUrl(txUrl).setAddressUrl(addressUrl).build();
    }
}
