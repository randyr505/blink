package com.nashlincoln.blink.model;

import java.util.List;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.nfc.NfcCommand;
import com.nashlincoln.blink.event.Event;
import java.util.ArrayList;
// KEEP INCLUDES END
/**
 * Entity mapped to table DEVICE.
 */
public class Device {

    private Long id;
    private String name;
    private Integer state;
    private String interconnect;
    private String attributableType;
    private Long deviceTypeId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient DeviceDao myDao;

    private DeviceType deviceType;
    private Long deviceType__resolvedKey;

    private List<Attribute> attributes;

    // KEEP FIELDS - put your custom fields here
    public static final String KEY = "Device";
    public static final String ATTRIBUTABLE_TYPE = "Device";
    // KEEP FIELDS END

    public Device() {
    }

    public Device(Long id) {
        this.id = id;
    }

    public Device(Long id, String name, Integer state, String interconnect, String attributableType, Long deviceTypeId) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.interconnect = interconnect;
        this.attributableType = attributableType;
        this.deviceTypeId = deviceTypeId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDeviceDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getInterconnect() {
        return interconnect;
    }

    public void setInterconnect(String interconnect) {
        this.interconnect = interconnect;
    }

    public String getAttributableType() {
        return attributableType;
    }

    public void setAttributableType(String attributableType) {
        this.attributableType = attributableType;
    }

    public Long getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(Long deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    /** To-one relationship, resolved on first access. */
    public DeviceType getDeviceType() {
        Long __key = this.deviceTypeId;
        if (deviceType__resolvedKey == null || !deviceType__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DeviceTypeDao targetDao = daoSession.getDeviceTypeDao();
            DeviceType deviceTypeNew = targetDao.load(__key);
            synchronized (this) {
                deviceType = deviceTypeNew;
            	deviceType__resolvedKey = __key;
            }
        }
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        synchronized (this) {
            this.deviceType = deviceType;
            deviceTypeId = deviceType == null ? null : deviceType.getId();
            deviceType__resolvedKey = deviceTypeId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Attribute> getAttributes() {
        if (attributes == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AttributeDao targetDao = daoSession.getAttributeDao();
            List<Attribute> attributesNew = targetDao._queryDevice_Attributes(id, attributableType);
            synchronized (this) {
                if(attributes == null) {
                    attributes = attributesNew;
                }
            }
        }
        return attributes;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetAttributes() {
        attributes = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here

    public void setName(String name, boolean sync) {
        setName(name);
        if (sync) {
            state = BlinkApp.STATE_NAME_SET;
            update();
        }
    }

    public void flushAttributes() {
        AttributeDao attributeDao = BlinkApp.getDaoSession().getAttributeDao();
        for (Attribute attribute : getAttributes()) {
            attribute.setAttributableId(id);
            attribute.setAttributableType(ATTRIBUTABLE_TYPE);

            attributeDao.insertOrReplace(attribute);
        }
    }

    public void setNominal() {
        switch (state) {
            case BlinkApp.STATE_UPDATED:
                for (Attribute attribute : getAttributes()) {
                    attribute.onSync();
                }
                state = BlinkApp.STATE_NOMINAL;
                update();
                break;

            case BlinkApp.STATE_REMOVED:
                deleteWithReferences();
                break;

            case BlinkApp.STATE_ADDED:
//                Syncro.getInstance().refreshDevices();
                state = BlinkApp.STATE_NOMINAL;
                update();
                break;
        }
    }

    public void deleteWithReferences() {
        for (Attribute attribute : getAttributes()) {
            attribute.delete();
        }

        GroupDeviceDao groupDeviceDao = BlinkApp.getDaoSession().getGroupDeviceDao();
        List<GroupDevice> groupDevices = groupDeviceDao.queryBuilder()
                .where(GroupDeviceDao.Properties.DeviceId.eq(getId()))
                .list();

        for (GroupDevice groupDevice : groupDevices) {
            groupDevice.delete();
        }
        Event.broadcast(Group.KEY);

        SceneDeviceDao sceneDeviceDao = BlinkApp.getDaoSession().getSceneDeviceDao();
        List<SceneDevice> sceneDevices = sceneDeviceDao.queryBuilder()
                .where(SceneDeviceDao.Properties.DeviceId.eq(getId()))
                .list();

        for (SceneDevice sceneDevice : sceneDevices) {
            sceneDevice.deleteWithReferences();
        }
        Event.broadcast(Scene.KEY);

        delete();
    }

    public void setAttribute(Long attributeTypeId, String value) {
        for (Attribute attribute : getAttributes()) {
            if (attribute.getAttributeTypeId().equals(attributeTypeId)) {
                attribute.setValue(value);
                attribute.update();
            }
        }
    }

    public void setLevel(int level) {
        if (getAttributes().size() < 2) {
            return;
        }
        Attribute attribute = getAttributes().get(1);
        if (attribute.getInt() != level) {
            attribute.setValueLocal(level);
            state = BlinkApp.STATE_UPDATED;
            update();
        }
    }

    public void setOn(boolean on) {
        if (getAttributes().size() < 1) {
            return;
        }
        Attribute attribute = getAttributes().get(0);
        if (on != attribute.getBool()) {
            attribute.setValueLocal(on);
            state = BlinkApp.STATE_UPDATED;
            update();
        }
    }

    public boolean isOn() {
        if (getAttributes().size() < 1) {
            return false;
        }
        return getAttributes().get(0).getBool();
    }

    public int getLevel() {
        if (getAttributes().size() < 2) {
            return 0;
        }
        return getAttributes().get(1).getInt();
    }

    public String toNfc() {
        List<NfcCommand> commands = new ArrayList<>();
        List<NfcCommand.Update> updates = new ArrayList<>();

        for (Attribute attribute : getAttributes()) {
            NfcCommand.Update update = new NfcCommand.Update();
            update.i = attribute.getAttributeTypeId();
            update.v = attribute.getValue();
            updates.add(update);
        }

        NfcCommand command = new NfcCommand();
        command.d = getId();
        command.u = updates;
        commands.add(command);

        return BlinkApi.getGson().toJson(commands);
    }
    // KEEP METHODS END

}
