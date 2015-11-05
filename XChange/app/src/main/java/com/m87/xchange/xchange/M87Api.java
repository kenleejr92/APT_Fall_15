//**************************************************************************************************
// Copyright (C) 2015 M87, Inc. All Rights Reserved.
// Proprietary & Confidential
//
// This source code and the algorithms implemented therein constitute
// confidential information and may compromise trade secrets of M87, Inc.
//--------------------------------------------------------------------------------------------------
package com.m87.xchange.xchange;

import java.util.Collection;
import java.util.List;

import com.m87.sdk.*;

import android.app.Notification.Action;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class implements a wrapper to the M87 SDK for apps. Applications can use the SDK to
 * discover nearby devices that are one or more hops away. Future releases of the SDK will
 * allow apps to exchange short messages and rich media with nearby devices that are one or
 * more hops away.
 * <p/>
 * The M87 SDK uses a publish/subscribe paradigm, where a publisher transmits an OTA-ID and a
 * subscriber receives OTA-IDs. Future releases of the SDK will allow a publisher to transmit
 * expressions and a subscriber to receive (and filter based on application-defined criteria)
 * expressions. Expressions can be used by applications to broadcast their needs/services; see
 * LTE Direct expressions for more details.
 * <p/>
 * M87Api simplifies interaction with the M87 SDK by abstracting the connecting, disconnecting
 * and event handling chores. An app should instantiate a single M87Api object in it's main
 * activity. M87Api will handle converting all asynchronous events from the SDK to the
 * application's main looper, allowing any callback processing to run in the app's thread
 * context.
 * <p/>
 * The app must pass in an implementation of the {@link M87Callbacks M87Callbacks}
 * interface to receive events from the SDK.
 */
public class M87Api
{
    //**************************************************************************************************
    // Private members
    //--------------------------------------------------------------------------------------------------
    private static final String TAG = "M87";

    private static final int MSG_SUCCESS                 = 87001;
    private static final int MSG_FAILURE                 = 87002;
    private static final int MSG_EVENT                   = 87003;
    private static final int MSG_NEAR_ENTRY              = 87004;
    private static final int MSG_NEAR_TABLE              = 87005;
    private static final int MSG_NEAR_MSG_TABLE          = 87006;
    private static final int MSG_PUBLISH_STATUS          = 87007;
    private static final int MSG_SUBSCRIBE_STATUS        = 87008;
    private static final int MSG_PUBLISH_CANCEL_STATUS   = 87009;
    private static final int MSG_SUBSCRIBE_CANCEL_STATUS = 87010;
    private static final int MSG_NEAR_MSG_ENTRY          = 87011;
    private static final int MSG_MSG_TX_STATUS           = 87012;

    private M87Callbacks callbacks = null;
    private M87SdkManager mgr = null;
    private boolean isInitialized = false;

    //------------------------------------------------------------------------------
    // M87Api must be instantiated from the main activity thread so this handler
    // attaches to the application's main looper
    //------------------------------------------------------------------------------
    private Handler app = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
                 if (msg.what == MSG_SUCCESS                 ) appSuccess               (msg);
            else if (msg.what == MSG_FAILURE                 ) appFailure               (msg);
            else if (msg.what == MSG_EVENT                   ) appEvent                 (msg);
            else if (msg.what == MSG_NEAR_ENTRY              ) appNearEntry             (msg);
            else if (msg.what == MSG_NEAR_TABLE              ) appNearTable             (msg);
            else if (msg.what == MSG_NEAR_MSG_TABLE          ) appNearMsgTable          (msg);
            else if (msg.what == MSG_PUBLISH_STATUS          ) appPublishStatus         (msg);
            else if (msg.what == MSG_SUBSCRIBE_STATUS        ) appSubscribeStatus       (msg);
            else if (msg.what == MSG_PUBLISH_CANCEL_STATUS   ) appPublishCancelStatus   (msg);
            else if (msg.what == MSG_SUBSCRIBE_CANCEL_STATUS ) appSubscribeCancelStatus (msg);
            else if (msg.what == MSG_NEAR_MSG_ENTRY          ) appNearMsgEntry          (msg);
            else if (msg.what == MSG_MSG_TX_STATUS           ) appMsgTxStatus           (msg);
            else return;

            super.handleMessage(msg);
        }
    };

    /**
     * Constructor to initialize M87 API
     *
     * @param ctx  application context
     * @param cbs  callbacks for receiving SDK events and status
     * @since      API level 1
     */
    public M87Api(Context ctx, M87Callbacks cbs)
    {
        mgr = new M87SdkManager(ctx, new M87ApiCallbacks());
        callbacks = cbs;
    }

    /**
     * Initializes M87SdkManager
     *
     * @param  ctx  application context
     * @return      status of the initialization
     * @since       API level 1
     */
    public boolean initialize(Context ctx)
    {
        if (isInitialized)
        {
            Log.d(TAG, "M87 SDK is already initialized");
            return true;

        }

        if (mgr == null)
        {
            Log.d(TAG, "Could not initialize M87 SDK: manager is NULL");
            return false;
        }

        try
        {
            Log.d(TAG, "Initializing M87 SDK");
            isInitialized = mgr.initialize(ctx);
        }
        catch (Exception e)
        {
            Log.d(TAG, "An exception occurred initializing SDK: " + e.getMessage());
        }

        if (! isInitialized) Log.d(TAG, "M87 SDK failed to initialize: ");

        return isInitialized;
    }

    /**
     * Shuts down the M87 manager
     *
     * @since  API level 1
     */
    public void shutdown()
    {
        if (mgr != null && isInitialized) mgr.shutdown();
        isInitialized = false;
    }

    /**
     * Checks whether id is valid for the M87 network
     * <p/>
     * id must be an integer between 1 and 100000, unique for each user of the app. Future releases of
     * the SDK will allow larger IDs.
     *
     * @param  id  ID to validate
     * @return     whether id is valid
     * @since      API level 1
     */
    public static boolean isIdValid(int id)
    {
        return (id > 0 && id < 100000);
    }

    //**************************************************************************************************
    // Private classes
    //--------------------------------------------------------------------------------------------------
    private class M87ApiCallbackStatus
    {
        public M87Action     a;
        public String        message;
        public M87StatusCode code;
        public String        v;
    }

    //-----------------------------------------------------------------------------------
    // Callbacks from the SDK. Send these to our Handler to get them on the main thread.
    //-----------------------------------------------------------------------------------
    private class M87ApiCallbacks implements M87Callbacks
    {
        @Override
        public void onSuccess(M87Action a, String message)
        {
            M87ApiCallbackStatus st = new M87ApiCallbackStatus();
            st.a       = a;
            st.message = message;

            Message msg = Message.obtain();
            msg.what    = MSG_SUCCESS;
            msg.obj     = st;

            app.sendMessage(msg);
        }

        public void onFailure(M87Action a, String message, M87StatusCode code, String v)
        {
            M87ApiCallbackStatus st = new M87ApiCallbackStatus();
            st.a       = a;
            st.message = message;
            st.code    = code;
            st.v       = v;

            Message msg = Message.obtain();
            msg.what    = MSG_FAILURE;
            msg.obj     = st;

            app.sendMessage(msg);
        }

        public void onEvent(M87Event evt)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_EVENT;
            msg.obj     = evt;

            app.sendMessage(msg);
        }

        public void onNearEntry(M87NearEntry entry, M87NearEntryState state)
        {
            Message msg = Message.obtain();
            msg.what = MSG_NEAR_ENTRY;
            msg.obj = entry;
            msg.arg1 = state.ordinal();
            app.sendMessage(msg);
        }

        public void onNearMsgEntry(M87NearMsgEntry entry, M87NearEntryState state)
        {
            Message msg = Message.obtain();
            msg.what = MSG_NEAR_MSG_ENTRY;
            msg.obj = entry;
            msg.arg1 = state.ordinal();
            app.sendMessage(msg);
        }

        public void onNearTable(M87NearEntry[] neighbors)
        {
            Message msg = Message.obtain();
            msg.what = MSG_NEAR_TABLE;
            msg.obj = neighbors;
            app.sendMessage(msg);
        }

        public void onNearMsgTable(M87NearMsgEntry[] nearMsg)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_NEAR_MSG_TABLE;
            msg.obj     = nearMsg;
            app.sendMessage(msg);
        }

        public void onNearMsgTxStatus(byte[] status)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_MSG_TX_STATUS;
            msg.obj     = status;
            app.sendMessage(msg);
        }

        public void onNearPublishStatus(int status)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_PUBLISH_STATUS;
            msg.arg1    = status;
            app.sendMessage(msg);
        }

        public void onNearSubscribeStatus(int status)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_SUBSCRIBE_STATUS;
            msg.arg1    = status;
            app.sendMessage(msg);
        }

        public void onNearPublishCancelStatus(int status)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_PUBLISH_CANCEL_STATUS;
            msg.arg1    = status;
            app.sendMessage(msg);
        }

        public void onNearSubscribeCancelStatus(int status)
        {
            Message msg = Message.obtain();
            msg.what    = MSG_SUBSCRIBE_CANCEL_STATUS;
            msg.arg1    = status;
            app.sendMessage(msg);
        }
    };

    //**************************************************************************************************
    // Methods to call the SDK
    //--------------------------------------------------------------------------------------------------
    /**
     * Gets the API level for the M87 SDK
     *
     * @return  API level for the M87 SDK
     * @since   API level 1
     */
    public int apiLevel()
    {
        if (! isInitialized) return -1;
        return mgr.apiLevel();
    }

    /**
     * Subscribes to the M87 SDK
     * <p/>
     * Enables the application to discover nearby devices. Future releases will allow apps to
     * receive messages and expressions from nearby devices.
     *
     * @param  ttl       time to live (<i>future extension; currently not supported</i>)
     * @param  exprCode  expression code (<i>future extension; currently not supported</i>)
     * @param  exprMask  expression mask (<i>future extension; currently not supported</i>)
     * @return           0 if the request was submitted successfully
     * @see              M87Callbacks#onNearSubscribeStatus
     * @see              M87Callbacks#onNearEntry
     * @see              M87Callbacks#onNearMsgEntry
     * @see              M87Callbacks#onNearTable
     * @see              M87Callbacks#onNearMsgTable
     * @since            API level 1
     */
    public int nearSubscribe(int ttl, byte[] exprCode, byte[] exprMask)
    {
        if (! isInitialized) return -1;
        return mgr.nearSubscribe(ttl, exprCode, exprMask);
    }

    /**
     * Publishes to the M87 SDK
     * <p/>
     * Enables the application to be discovered by nearby devices. Future releases will allow
     * apps to transmit messages and expressions to nearby devices.
     *
     * @param  ttl       time to live (<i>future extension; currently not supported</i>)
     * @param  exprCode  expression code (<i>future extension; currently not supported</i>)
     * @param  maxHops   max number of hops (<i>future extension; currently not supported</i>)
     * @param  otaId     over-the-air (OTA) ID that allows nearby devices to discover the app
     * @return           0 if the request was submitted successfully
     * @see              M87Callbacks#onNearPublishStatus
     * @see              #isIdValid
     * @since            API level 1
     */
    public int nearPublish(int ttl, byte[] exprCode, int maxHops, int otaId)
    {
        if (! isInitialized) return -1;
        return mgr.nearPublish(ttl, exprCode, maxHops, otaId);
    }

    /**
     * Cancels subscription to the M87 SDK
     * <p/>
     * Once subscription is canceled, the application will not receive events corresponding to
     * nearby devices. The app must subscribe again to receive these events.
     *
     * @param  ttl       time to live (<i>future extension; currently not supported</i>)
     * @param  exprCode  expression code (<i>future extension; currently not supported</i>)
     * @param  exprMask  expression mask (<i>future extension; currently not supported</i>)
     * @return           0 if the request was submitted successfully
     * @see              M87Callbacks#onNearSubscribeCancelStatus
     * @see              #nearSubscribe
     * @since            API level 1
     */
    public int nearSubscribeCancel(int ttl, byte[] exprCode, byte[] exprMask)
    {
        if (! isInitialized) return -1;
        return mgr.nearSubscribeCancel(ttl, exprCode, exprMask);
    }

    /**
     * Cancels publish to the M87 SDK
     * <p/>
     * Once publish is canceled, nearby devices will not receive events corresponding to the
     * application. The app must publish again so that nearby devices can receive these events.
     *
     * @param  ttl       time to live (<i>future extension; currently not supported</i>)
     * @param  exprCode  expression code (<i>future extension; currently not supported</i>)
     * @return           0 if the request was submitted successfully
     * @see              M87Callbacks#onNearPublishCancelStatus
     * @see              #nearPublish
     * @since            API level 1
     */
    public int nearPublishCancel(int ttl, byte[] exprCode)
    {
        if (! isInitialized) return -1;
        return mgr.nearPublishCancel(ttl, exprCode);
    }

    /**
     * Sends a broadcast message to nearby devices
     * <p/>
     * <i>Future extension. Currently not supported.</i>
     *
     * @param  msg  message to broadcast to nearby devices
     * @return      0 if the request was submitted successfully
     * @since       API level 2
     */
    public int nearMsgBroadcast(String msg)
    {
        if (! isInitialized) return -1;
        return mgr.nearMsgBroadcast(msg);
    }

    /**
     * Sends a direct message to one or more nearby devices
     * <p/>
     * <i>Future extension. Currently not supported.</i>
     *
     * @param  id   OTA-ID(s) of one or more nearby devices
     * @param  msg  message to send to one or more nearby devices
     * @return      0 if the request was submitted successfully
     * @since       API level 2
     */
    public int nearMsgSend(int id, String msg)
    {
        if (! isInitialized) return -1;
        return mgr.nearMsgSend(id, msg);
    }

    /**
     * Sets the specified metric
     * <p/>
     * <i>Internal API. Not for external use.</i>
     *
     * @param  name   name of metric
     * @param  value  value of metric
     * @return        0 if the request was submitted successfully
     * @since         API level 1
     */
    public int setMetric(String name, int value)
    {
        if (! isInitialized) return -1;
        return mgr.setMetric(name, value);
    }

    //**************************************************************************************************
    // Incoming SDK event handlers. These are called on the app's main thread via our handler
    //--------------------------------------------------------------------------------------------------
    void appSuccess(Message msg)
    {
        M87ApiCallbackStatus st = (M87ApiCallbackStatus)msg.obj;

        callbacks.onSuccess(st.a, st.message);
    }

    void appFailure(Message msg)
    {
        M87ApiCallbackStatus st = (M87ApiCallbackStatus)msg.obj;

        callbacks.onFailure(st.a, st.message, st.code, st.v);
    }

    void appEvent(Message msg)
    {
        M87Event evt = (M87Event)msg.obj;

        callbacks.onEvent(evt);
    }

    void appNearEntry(Message msg)
    {
        M87NearEntryState state = M87NearEntryState.values()[msg.arg1];

        callbacks.onNearEntry((M87NearEntry)msg.obj, state);
    }

    void appNearMsgEntry(Message msg)
    {
        M87NearEntryState state = M87NearEntryState.values()[msg.arg1];

        callbacks.onNearMsgEntry((M87NearMsgEntry)msg.obj, state);
    }

    void appNearTable(Message msg)
    {
        callbacks.onNearTable((M87NearEntry[])msg.obj);
    }

    void appNearMsgTable(Message msg)
    {
        callbacks.onNearMsgTable((M87NearMsgEntry[])msg.obj);
    }

    void appMsgTxStatus(Message msg)
    {
        callbacks.onNearMsgTxStatus((byte[])msg.obj);
    }

    void appPublishStatus(Message msg)
    {
        callbacks.onNearPublishStatus(msg.arg1);
    }

    void appSubscribeStatus(Message msg)
    {
        callbacks.onNearSubscribeStatus(msg.arg1);
    }

    void appPublishCancelStatus(Message msg)
    {
        callbacks.onNearPublishCancelStatus(msg.arg1);
    }

    void appSubscribeCancelStatus(Message msg)
    {
        callbacks.onNearSubscribeCancelStatus(msg.arg1);
    }
}
