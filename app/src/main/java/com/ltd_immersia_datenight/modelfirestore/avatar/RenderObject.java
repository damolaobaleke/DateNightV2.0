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

package com.ltd_immersia_datenight.modelfirestore.avatar;

import java.io.Serializable;

public class RenderObject implements Serializable {
    String[] renders ;

    public RenderObject(String[] renders){
        this.renders = renders;
    }

    public String[] getRender() {
        return renders;
    }

    public String[] getRenders() {
        return renders;
    }

    public void setRenders(String[] renders) {
        this.renders = renders;
    }
}

