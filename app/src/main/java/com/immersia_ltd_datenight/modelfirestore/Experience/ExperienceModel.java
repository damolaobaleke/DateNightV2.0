/*
 * Copyright 2020 Damola Obaleke. All rights reserved.
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

package com.immersia_ltd_datenight.modelfirestore.Experience;

public class ExperienceModel {
    String description;
    String environmentPreviewUrl; //3D
    String environmentUrl;
    int duration;
    String previewImageUrl;
    int price;
    String name;
    String id;

    public ExperienceModel(String description, String previewImage, int duration, int price, String environmentUrl, String name, String id) {
        this.description = description;
        this.environmentPreviewUrl = previewImage;
        this.duration = duration;
        this.environmentUrl = environmentUrl;
        this.price = price;
        this.name = name;
        this.id = id;
    }

    /*No argument constructor*/
    public ExperienceModel() {
    }

    public String getDescription() {
        return description;
    }

    public String getEnvironmentPreviewUrl() {
        return environmentPreviewUrl;
    }

    public String getEnvironmentUrl() {
        return environmentUrl;
    }

    public int getDuration() {
        return duration;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
