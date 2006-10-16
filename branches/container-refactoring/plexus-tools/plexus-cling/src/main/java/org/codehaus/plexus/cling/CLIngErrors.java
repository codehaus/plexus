/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling;

/**
 * Constants class containing all CLIng-specific runtime error codes.
 * 
 * @author jdcasey
 */
public final class CLIngErrors
{

    public static final int ERROR_STARTING_EMBEDDER = -10000;

    public static final int ERROR_LOOKING_UP_LAUNCHER = -10001;

    public static final int ERROR_PARSING_ARGS = -1;

    public static final int ERROR_ACCESSING_MAIN_METHOD = -2;

    public static final int ERROR_MAIN_METHOD_HAD_ILLEGAL_ARGUMENT = -4;

    public static final int ERROR_MAIN_METHOD_NOT_ACCESSIBLE_FOR_INVOCATION = -5;

    public static final int ERROR_MAIN_METHOD_FAILED_TO_EXECUTE = -6;

    public static final int ERROR_SETTING_OBJECT_PROPERTY = -7;

    public static final int ERROR_REQUIRED_OPTIONS_NOT_SATISFIED = -8;

    public static final int ERROR_MAIN_CLASS_NOT_FOUND = -9;

    public static final int ERROR_MAIN_CLASS_NOT_INSTANTIABLE = -10;

    public static final int ERROR_CREATING_DUPLICATE_CLASSREALM = -11;

    public static final int ERROR_RETRIEVING_APPXML_CANONICAL_PATH = -12;

    public static final int ERROR_OPENING_APPXML = -13;

    public static final int ERROR_PARSING_APPXML = -14;

    public static final int ERROR_BUILDING_APPXML = -15;

    public static final int ERROR_VALIDATING_APPXML = -16;

    public static final int ERROR_BUILDING_APPMODEL_FROM_APPXML = -17;

    public static final int ERROR_BUILDING_DEFAULT_LOCAL_REPO_PATH = -18;

    public static final int ERROR_RESOLVING_CLASSPATH_ENTRY = -19;

    public static final int ERROR_CONSTRUCTING_LOCAL_REPO_URL = -20;

    public static final int ERROR_FINDING_MAIN_METHOD = -21;

    public static final int ERROR_CANONICALIZING_APPDIR = -22;

    public static final int UNHANDLED_EXCEPTION = -22;

    private CLIngErrors()
    {
    }

}