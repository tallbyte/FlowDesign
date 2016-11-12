package com.tallbyte.flowdesign.core__;

import com.tallbyte.flowdesign.core.FlowException;
import com.tallbyte.flowdesign.core__.element.LinearFlow;

import java.sql.Connection;

/**
 * Created on 2016-10-27.
 */
public class StringInserter extends LinearFlow<String, Boolean> {

    protected Connection connection;

    public StringInserter(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean invoke(String input) throws FlowException {
        // preparedStatement.execute.... bla bla bla
        return true;
    }
}
