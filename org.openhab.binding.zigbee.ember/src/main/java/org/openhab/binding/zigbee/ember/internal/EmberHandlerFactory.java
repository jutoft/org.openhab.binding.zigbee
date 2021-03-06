/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.zigbee.ember.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.zigbee.ember.EmberBindingConstants;
import org.openhab.binding.zigbee.ember.handler.EmberHandler;
import org.openhab.binding.zigbee.handler.ZigBeeCoordinatorHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link EmberHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Chris Jackson - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "org.openhab.binding.zigbee.ember")
@NonNullByDefault
public class EmberHandlerFactory extends BaseThingHandlerFactory {
    private Map<ThingUID, ServiceRegistration> coordinatorHandlerRegs = new HashMap<>();

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(EmberBindingConstants.THING_TYPE_EMBER);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        ZigBeeCoordinatorHandler emberHandler = null;
        if (thingTypeUID.equals(EmberBindingConstants.THING_TYPE_EMBER)) {
            emberHandler = new EmberHandler((Bridge) thing);
        }

        if (emberHandler != null) {
            coordinatorHandlerRegs.put(emberHandler.getThing().getUID(), bundleContext.registerService(
                    ZigBeeCoordinatorHandler.class.getName(), emberHandler, new Hashtable<String, Object>()));

            return emberHandler;
        }

        return null;
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof EmberHandler) {
            ServiceRegistration coordinatorHandlerReg = coordinatorHandlerRegs.get(thingHandler.getThing().getUID());
            if (coordinatorHandlerReg != null) {
                coordinatorHandlerReg.unregister();
                coordinatorHandlerRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }
}
