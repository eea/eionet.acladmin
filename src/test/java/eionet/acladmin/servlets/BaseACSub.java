package eionet.acladmin.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eionet.acladmin.Names;
import eionet.acl.SignOnException;

/**
 * Subclassing BaseAC to access the methods for testing.
 */
public class BaseACSub extends BaseAC implements Names {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }

}
