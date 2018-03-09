/*
 * Copyright (c) 2018 Jalotsav
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jalotsav.brainythings.models;

/**
 * Created by Jalotsav on 2/7/2018.
 */

public class MdlDevices {

    private String id, type, name, gpio;
    private MdlTraits traits;

    public MdlDevices() {
    }

    public MdlDevices(String id, String type, String name, String gpio, MdlTraits traits) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.gpio = gpio;
        this.traits = traits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getGpio() {
        return gpio;
    }

    public MdlTraits getTraits() {
        return traits;
    }
}
