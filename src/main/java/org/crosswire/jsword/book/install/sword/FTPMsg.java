package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.MsgBase;

final class FTPMsg extends MsgBase 
{
  private static final MsgBase AUTH_REFUSED = new FTPMsg("SwordInstaller.AuthRefused");
  private static final MsgBase CONNECT_REFUSED = new FTPMsg("SwordInstaller.ConnectRefused");
  private static final MsgBase CWD_REFUSED = new FTPMsg("SwordInstaller.CWDRefused");
  private static final MsgBase DOWNLOAD_REFUSED = new FTPMsg("SwordInstaller.DownloadRefused");
  private static final MsgBase URL_AT_COUNT = new FTPMsg("SwordInstallerFactory.URLAtCount");
  private static final MsgBase URL_COLON_COUNT = new FTPMsg("SwordInstallerFactory.URLColonCount");
  
  private static String name;
  
  private FTPMsg(String nam) { name = nam; }
  
  public String getName()    { return name; }
}
