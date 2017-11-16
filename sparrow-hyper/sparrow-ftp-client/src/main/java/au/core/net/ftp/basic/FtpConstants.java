package au.core.net.ftp.basic;

/**
 * This class provides some constant used by FtpConnection.
 */
public interface FtpConstants {
	/** return success code */
	public final static char POSITIVE = '2';
	/** return error code */
	public final static char NEGATIVE = '5';
	/** has more line to analyzer */
	public final static char MORE_LINE = '-';
	/** need input next command to operate, go on doing other operation */
	public final static char PROCEED = '3'; // "350";
	/** negative command */
	public final static char NEGATIVE2 = '4';
	/** transfer setting ,binary */
	public final static char BINARY = 'I';
	/** ASCII transfer */
	public final static char ASCII = 'A';
	/** stream transfer */
	public final static char STREAM = 'S';
	/** file status */
	public final static char FILE_STATUS = '1';

	// public static final String LIST = "LIST -laL";
	/** Possible upload() / download() return code */
	public static final int TRANSFER_FAILED = -1;

	/** Possible upload() / download() return code */
	public static final int TRANSFER_STOPPED = -2;

	/** Possible upload() / download() / remove() return code */
	public static final int MKDIR_FAILED = -3;

	/** Possible upload() / download() return code */
	public static final int PERMISSION_DENIED = -4;

	/** Possible upload() / download() / exists() return code */
	public static final int CHDIR_FAILED = -5;

	/** Possible login() return code */
	public final static int WRONG_LOGIN_DATA = -6;

	/** Possible login() return code */
	public final static int OFFLINE = -7;

	/** Possible login() / remove() return code */
	public final static int GENERIC_FAILED = -8;

	/** Possible remove() return code */
	public final static int REMOVE_FAILED = -9;

	/** Possible upload() return code */
	public final static int RENAME_FAILED = -10;

	/** Possible exists() return code */
	public final static int FILE_NOT_FOUND = -11;

	/** Possible up/download() return code */
	public final static int DATACONNECTION_FAILED = -12;

	/** Possible upload() / download() return code */
	public static final int NEW_TRANSFER_SPAWNED = 0;

	/** Possible upload() / download() return code */
	public static final int TRANSFER_SUCCESSFUL = 1;

	/** Possible login() return code */
	public final static int LOGIN_OK = 2;

	/** Possible remove() return code */
	public final static int REMOVE_SUCCESSFUL = 3;

	/** Possible getPermissions() / exists() return code */
	public static final int R = 23;

	/** Possible getPermissions() / exists() return code */
	public static final int W = 42;

	/** Possible getPermissions() / exists() return code */
	public static final int DENIED = -666;

	// Access Control Commands

	/** User Name */
	public final static String USER = "USER";

	/** Password */
	public final static String PASS = "PASS";

	/** Change Working Directory */
	public final static String CWD = "CWD";

	/** Change to Parent Directory */
	public final static String CDUP = "CDUP";

	/** Logout */
	public final static String QUIT = "QUIT";

	// Transfer Parameter Commands

	/** Data Port */
	public final static String PORT = "PORT";

	/** Passive */
	public final static String PASV = "PASV";
	
	public final static String OPTS_UTF8_ON = "OPTS utf8 on";

	/**
	 * Representation Type (ASCII, EBCDIC, Image, Non-print, Telnet format
	 * effectors, Carriage Control, Local byte Byte size)
	 */
	public final static String TYPE = "TYPE";

	/** Transfer Mode (Stream, Block, Compressed) */
	public final static String MODE = "MODE";

	// FTP Service Commands

	/** Retrieve */
	public final static String RETR = "RETR";

	/** Store */
	public final static String STOR = "STOR";

	/** Restart start position */
	public final static String REST = "REST";

	/** Rename From rename file from */
	public final static String RNFR = "RNFR";

	/** Rename To */
	public final static String RNTO = "RNTO";

	/** Abort ,cancel */
	public final static String ABOR = "ABOR";

	/** Delete */
	public final static String DELE = "DELE";

	/** Remove Directory */
	public final static String RMD = "RMD";

	/** Make Directory */
	public final static String MKD = "MKD";

	/** Print Working Directory */
	public final static String PWD = "PWD";

	public final static String LIST = "LIST";

	/** Name List */
	public final static String NLST = "NLST";

	/** Site Parameters */
	public final static String SITE = "SITE";

	/** System */
	public final static String SYST = "SYST";

	/** Status */
	public final static String STAT = "STAT";

	/** Help */
	public final static String HELP = "HELP";

	/** NOOP */
	public final static String NOOP = "NOOP";

	// The following commands were introduced in rfc 2228 FTP Security
	// Extensions

	/** Authentication/Security Mechanism */
	public final static String AUTH = "AUTH";

	/** Restart marker reply */
	public final static String RC110 = "110";

	/** Data connection already open */
	public final static String RC125 = "125";

	/** File status okay; about to open data connection */
	public final static String RC150 = "150";

	// 2yz Positive Completion reply

	/** Command okay */
	public final static String FTP200_OK = "2";

	/** Command not implemented, superfluous at this site */
	public final static String RC202 = "202";

	/** System status, or system help reply */
	public final static String RC211 = "211";

	/** Directory status */
	public final static String RC212 = "212";

	/** File status */
	public final static String RC213 = "213";

	/** Help services */
	public final static String RC214 = "214";

	/** System type */
	public final static String FTP215_SYSTEM_TYPE = "215";

	/** Service ready for new user */
	public final static String FTP220_SERVICE_READY = "220";

	/** Service closing control connection */
	public final static String FTP221_SERVICE_CLOSING = "221";

	/** Data connection open; no transfer in progress */
	public final static String RC225 = "225";

	/** Closing data connection; requested file action successful */
	public final static String FTP226_CLOSING_DATA_REQUEST_SUCCESSFUL = "226";

	/** Entering Passive Mode */
	public final static String FTP227_ENTERING_PASSIVE_MODE = "227";

	/** User logged in, proceed */
	public final static String FTP230_LOGGED_IN = "2"; // 230

	/** Requested file action okay, completed */
	public final static String FTP250_COMPLETED = "250";

	/** "PATHNAME" created */
	public final static String FTP257_PATH_CREATED = "257";

	// 3yz Positive Intermediate reply

	/** User name okay, need password */
	public final static String FTP331_USER_OK_NEED_PASSWORD = "331";

	/** Need account for login */
	public final static String RC332 = "332";

	/** Requested file action pending further information */
	public final static String RC350 = "350";

	// 4yz Transient Negative Completion reply

	/** Service not available */
	public final static String RC421 = "421";

	/** Can't open data connection */
	public final static String RC425 = "425";

	/** Connection closed; transfer aborted */
	public final static String RC426 = "426";

	/** Requested file action not taken, file unavailable (file busy) */
	public final static String RC450 = "450";

	/** Requested action aborted: local error in processing */
	public final static String RC451 = "451";

	/** Requested action not taken, insufficient storage space in system */
	public final static String RC452 = "452";

	// 5yz Permanent Negative Completion reply

	/** Syntax error, command unrecognized */
	public final static String RC500 = "500";

	/** Syntax error in parameters or arguments */
	public final static String RC501 = "501";

	/** Command not implemented */
	public final static String RC502 = "502";

	/** Bad sequence of commands */
	public final static String RC503 = "503";

	/** Command not implemented for that parameter */
	public final static String RC504 = "504";

	/** Not logged in */
	public final static String RC530 = "530";

	/** Need account for storing files */
	public final static String RC532 = "532";

	/** Requested action not taken, file unavailable (file not found, no access) */
	public final static String RC550 = "550";

	/** Requested action aborted: page type unknown */
	public final static String RC551 = "551";

	/** Requested file action aborted, exceeded storage allocation */
	public final static String RC552 = "552";

	/** Requested action not taken, file name not allowed */
	public final static String RC553 = "553";
}
