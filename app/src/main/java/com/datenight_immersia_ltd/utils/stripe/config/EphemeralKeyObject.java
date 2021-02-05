/*
 * Copyright 2021 Damola Obaleke. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datenight_immersia_ltd.utils.stripe.config;

import java.util.List;

public class EphemeralKeyObject {
    String id;
    String object;
    List<EphemeralStripeCustomer> associated_objects;
    int created;
    int expires;
    boolean livemode;
    String secret;

    public EphemeralKeyObject(String id, String object, List<EphemeralStripeCustomer> associated_objects, int created, int expires, boolean livemode, String secret) {
        this.id = id;
        this.object = object;
        this.associated_objects = associated_objects;
        this.created = created;
        this.expires = expires;
        this.livemode = livemode;
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public List<EphemeralStripeCustomer> getAssociated_objects() {
        return associated_objects;
    }

    public int getCreated() {
        return created;
    }

    public int getExpires() {
        return expires;
    }

    public boolean isLivemode() {
        return livemode;
    }

    public String getSecret() {
        return secret;
    }
}
