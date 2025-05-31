/******************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2002 TILAB S.p.A.
 *
 * This file is donated by Acklin B.V. to the JADE project.
 *
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * ***************************************************************/
package jade.tools.testagent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import jade.tools.gui.ACLPanel;
import jade.tools.gui.ACLTracePanel;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

/**
 *  This class is builds up the GUI of the TestAgent
 *
 * @author     Chris van Aart - Acklin B.V., the Netherlands
 * @created    May 6, 2002
 */

public class TestAgentFrame extends JFrame {

  /**
   *  Constructor for the TestAgentFrame object
   *
   * @param  agent  Description of Parameter
   */
  public TestAgentFrame(TestAgent agent) {
    getImages();
    this.agent = agent;
    aclPanel = new ACLPanel(agent);
    aclTreePanel = new ACLTracePanel(agent);
    try {
      jbInit();
      this.setSize(600, 600);
      this.setTitle("Jade TestAgent beta - " + agent.getName());
      this.setFrameIcon("images/dummy.gif");
      this.setVisible(true);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   *  Gets the ItsMsg attribute of the TestAgentFrame object
   *
   * @return    The ItsMsg value
   */
  public ACLMessage getItsMsg() {
    return aclPanel.getItsMsg();
  }


  public void getImages() {

    try {
      newIcon =
        new ImageIcon(this.getClass().getResource("images/new.gif"));
      openIcon =
        new ImageIcon(this.getClass().getResource("images/open.gif"));
      saveIcon =
        new ImageIcon(this.getClass().getResource("images/save.gif"));
      sendIcon =
        new ImageIcon(this.getClass().getResource("images/send.gif"));
      readQueueIcon =
        new ImageIcon(this.getClass().getResource("images/readqueue.gif"));
      saveQueueIcon =
        new ImageIcon(this.getClass().getResource("images/writequeue.gif"));
      currentIcon =
        new ImageIcon(this.getClass().getResource("images/current.gif"));
      replyIcon =
        new ImageIcon(this.getClass().getResource("images/reply.gif"));
      viewIcon =
        new ImageIcon(this.getClass().getResource("images/inspect.gif"));
      deleteIcon =
        new ImageIcon(this.getClass().getResource("images/delete.gif"));
      statisticsIcon =
        new ImageIcon(this.getClass().getResource("images/book.gif"));
      quitIcon =
        new ImageIcon(this.getClass().getResource("images/quit.gif"));
      systemIcon =
        new ImageIcon(this.getClass().getResource("images/system.gif"));
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }


  /**
   *  Sets the ItsMsg attribute of the TestAgentFrame object
   *
   * @param  msg  The new ItsMsg value
   */
  public void setItsMsg(ACLMessage msg) {
    aclPanel.setItsMsg(msg);
  }


  /**
   *  Sets the FrameIcon attribute of the TestAgentFrame object
   *
   * @param  iconpath  The new FrameIcon value
   */
  public void setFrameIcon(String iconpath) {
    ImageIcon image = new ImageIcon(this.getClass().getResource(iconpath));
    setIconImage(image.getImage());
  }


  /**
   *  Adds a feature to the MessageNode attribute of the TestAgentFrame object
   *
   * @param  msg        The feature to be added to the MessageNode attribute
   * @param  direction  The feature to be added to the MessageNode attribute
   */
  public void addMessageNode(String direction, ACLMessage msg) {
    aclTreePanel.addMessageNode(direction, msg);
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void helloWorldMenuItemActionPerformed(ActionEvent e) {
    agent.doHelloWorld();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void amsRegMenuItemActionPerformed(ActionEvent e) {
    agent.doRegisterAMS();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void systemMenuItemActionPerformed(ActionEvent e) {
    agent.doSystemOut();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void exitMenuItemActionPerformed(ActionEvent e) {
    agent.doExit();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void newButtonActionPerformed(ActionEvent e) {
    agent.doNewMessage();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void sendButtonActionPerformed(ActionEvent e) {
    agent.sendMessage();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void pingLausanneMenuItemActionPerformed(ActionEvent e) {
    agent.doLausannePing();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void amsDeregMenuItemActionPerformed(ActionEvent e) {
    agent.doDeRegisterAMS();
  }


  /**
   *  Description of the Method
   *
   * @param  e  Description of Parameter
   */
  void amsSearchMenuItemActionPerformed(ActionEvent e) {
    agent.doSearchAMS();
  }


  void dfRegMenuItemActionPerformed(ActionEvent e) {
    agent.doRegisterDF();
  }


  void dfDeregMenuItemActionPerformed(ActionEvent e) {
    agent.doDeregisterDF();
  }


  void dfSearchMenuItemActionPerformed(ActionEvent e) {
    agent.doSearchDF();
  }


  void aboutMenuItemActionPerformed(ActionEvent e) {
    new AboutFrame().setVisible(true);
  }


  void readQueueButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.loadQueue();
  }


  void writeQueueButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.saveQueue();
  }


  void currentButtonActionPerformed(ActionEvent e) {

    ACLMessage currentACL = this.aclTreePanel.getCurrentACL();
    if (currentACL != null) {
      this.aclPanel.setItsMsg((ACLMessage)currentACL.clone());
    }
  }


  void viewButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.doShowCurrentACL();
  }


  void deleteButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.deleteCurrent();
  }


  void statisticsButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.showStastistics();
  }


  void quitButtonActionPerformed(ActionEvent e) {
    agent.doDelete();
    System.exit(1);
  }


  void replyButtonActionPerformed(ActionEvent e) {
    agent.doReply();
  }


  void newMenuItemActionPerformed(ActionEvent e) {
    agent.doNewMessage();
  }


  void loadMenuItemActionPerformed(ActionEvent e) {
    this.aclPanel.loadACL();
  }


  void saveMenuItemActionPerformed(ActionEvent e) {
    this.aclPanel.saveACL();
  }


  void sendMenuItemActionPerformed(ActionEvent e) {
    agent.sendMessage();
  }


  void saveButtonActionPerformed(ActionEvent e) {
    this.aclPanel.saveACL();
  }


  void openButtonActionPerformed(ActionEvent e) {
    this.aclPanel.loadACL();
  }


  void saveQueueMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.saveQueue();
  }


  void claerQueueMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.clearACLModel();
  }


  void currentMenuItemActionPerformed(ActionEvent e) {
    ACLMessage currentACL = this.aclTreePanel.getCurrentACL();
    if (currentACL != null) {
      this.aclPanel.setItsMsg((ACLMessage)currentACL.clone());
    }

  }


  void replyMenuItemActionPerformed(ActionEvent e) {
    agent.doReply();
  }


  void deleteMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.deleteCurrent();
  }


  void statisticsMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.showStastistics();
  }


  void loadMsgMenuItemActionPerformed(ActionEvent e) {
    this.aclPanel.loadACL();
  }


  void saveMsgMenuItemActionPerformed(ActionEvent e) {
    this.aclPanel.saveACL();
  }


  void loadQMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.loadQueue();
  }


  void saveQMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.saveQueue();
  }


  void systemButtonActionPerformed(ActionEvent e) {
    this.aclTreePanel.doSystemOut();
  }


  void systemOutMenuItemActionPerformed(ActionEvent e) {
    this.aclPanel.doSystemOut();
  }


  void currentToOutMenuItemActionPerformed(ActionEvent e) {
    this.aclTreePanel.doSystemOut();
  }


  void pingRadioButtonMenuItemStateChanged(ChangeEvent e) {
    agent.pingBehaviour = pingRadioButtonMenuItem.isSelected();
  }


  void localPingMenuItemActionPerformed(ActionEvent e) {
    agent.doLocalPing();
  }


  /**
   *  Description of the Method
   *
   * @exception  Exception  Description of Exception
   */
  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder();
    this.getContentPane().setLayout(gridBagLayout1);
    fileMenu.setBackground(Color.white);
    fileMenu.setFont(new java.awt.Font("Dialog", 0, 12));
    fileMenu.setMnemonic('F');
    fileMenu.setText("File");
    exitMenuItem.setBackground(Color.white);
    exitMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    exitMenuItem.setMnemonic('X');
    exitMenuItem.setText("Exit");
    exitMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exitMenuItemActionPerformed(e);
        }
      });
    messagesMenu.setBackground(Color.white);
    messagesMenu.setFont(new java.awt.Font("Dialog", 0, 12));
    messagesMenu.setMnemonic('M');
    messagesMenu.setText("Message");
    helloWorldMenuItem.setBackground(Color.white);
    helloWorldMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    helloWorldMenuItem.setMnemonic('H');
    helloWorldMenuItem.setText("Hello world");
    helloWorldMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          helloWorldMenuItemActionPerformed(e);
        }
      });
    amsRegMenuItem.setBackground(Color.white);
    amsRegMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    amsRegMenuItem.setMnemonic('R');
    amsRegMenuItem.setText("AMSRegister");
    amsRegMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          amsRegMenuItemActionPerformed(e);
        }
      });
    amsDeregMenuItem.setBackground(Color.white);
    amsDeregMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    amsDeregMenuItem.setMnemonic('D');
    amsDeregMenuItem.setText("AMSDeregister");
    amsDeregMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          amsDeregMenuItemActionPerformed(e);
        }
      });
    amsSearchMenuItem.setBackground(Color.white);
    amsSearchMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    amsSearchMenuItem.setMnemonic('A');
    amsSearchMenuItem.setText("AMSSearch");
    amsSearchMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          amsSearchMenuItemActionPerformed(e);
        }
      });
    this.getContentPane().setBackground(Color.white);
    this.setJMenuBar(itsMenuBar);
    mainSplitPane.setForeground(Color.white);
    itsMenuBar.setBackground(Color.white);
    pingLausanneMenuItem.setBackground(Color.white);
    pingLausanneMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    pingLausanneMenuItem.setToolTipText("Ping to Lausannes PingAgent (works only when http package installed)");
    pingLausanneMenuItem.setMnemonic('P');
    pingLausanneMenuItem.setText("Ping to Lausanne ");
    pingLausanneMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          pingLausanneMenuItemActionPerformed(e);
        }
      });
    dfSearchMenuItem.setBackground(Color.white);
    dfSearchMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    dfSearchMenuItem.setMnemonic('D');
    dfSearchMenuItem.setText("DFSearch");
    dfSearchMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dfSearchMenuItemActionPerformed(e);
        }
      });
    dfRegMenuItem.setBackground(Color.white);
    dfRegMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    dfRegMenuItem.setText("DFRegister");
    dfRegMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dfRegMenuItemActionPerformed(e);
        }
      });
    dfDeregMenuItem.setBackground(Color.white);
    dfDeregMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    dfDeregMenuItem.setText("DFDeregister");
    dfDeregMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dfDeregMenuItemActionPerformed(e);
        }
      });
    helpMenu.setBackground(Color.white);
    helpMenu.setFont(new java.awt.Font("Dialog", 0, 12));
    helpMenu.setMnemonic('H');
    helpMenu.setText("Help");
    aboutMenuItem.setBackground(Color.white);
    aboutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutMenuItem.setForeground(new Color(0, 0, 132));
    aboutMenuItem.setMnemonic('A');
    aboutMenuItem.setText("About...");
    aboutMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          aboutMenuItemActionPerformed(e);
        }
      });
    leftPanel.setLayout(gridBagLayout2);
    rightPanel.setLayout(gridBagLayout3);
    writeQueueButton.setBorder(border1);
    writeQueueButton.setToolTipText("Save ACLMessage Trace");
    writeQueueButton.setIcon(saveQueueIcon);
    writeQueueButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          writeQueueButtonActionPerformed(e);
        }
      });
    readQueueButton.setBackground(Color.white);
    readQueueButton.setBorder(border1);
    readQueueButton.setToolTipText("Open ACLMessage trace");
    readQueueButton.setIcon(readQueueIcon);
    readQueueButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          readQueueButtonActionPerformed(e);
        }
      });
    openButton.setBackground(Color.white);
    openButton.setFont(new java.awt.Font("Dialog", 0, 11));
    openButton.setBorder(border1);
    openButton.setToolTipText("Open ACLMessage From File");
    openButton.setIcon(openIcon);
    openButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          openButtonActionPerformed(e);
        }
      });
    sendButton.setBackground(Color.white);
    sendButton.setFont(new java.awt.Font("Dialog", 0, 11));
    sendButton.setBorder(border1);
    sendButton.setToolTipText("Send ACLMessage");
    sendButton.setIcon(sendIcon);
    sendButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          sendButtonActionPerformed(e);
        }
      });
    newButton.setBackground(Color.white);
    newButton.setFont(new java.awt.Font("Dialog", 0, 11));
    newButton.setBorder(border1);
    newButton.setPreferredSize(new Dimension(29, 27));
    newButton.setToolTipText("New ACLMessage");
    newButton.setIcon(newIcon);
    newButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newButtonActionPerformed(e);
        }
      });
    saveButton.setBackground(Color.white);
    saveButton.setFont(new java.awt.Font("Dialog", 0, 11));
    saveButton.setBorder(border1);
    saveButton.setToolTipText("Save ACLMessage To File");
    saveButton.setIcon(saveIcon);
    saveButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveButtonActionPerformed(e);
        }
      });
    messageToolBar.setBackground(Color.white);
    messageToolBar.setFloatable(false);
    aclTreeToolBar.setBackground(Color.white);
    aclTreeToolBar.setFloatable(false);
    currentButton.setBorder(border1);
    currentButton.setToolTipText("Set Selected ACLMessage as current ACLMessage");
    currentButton.setIcon(currentIcon);
    currentButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          currentButtonActionPerformed(e);
        }
      });
    replyButton.setBorder(border1);
    replyButton.setToolTipText("Reply To Current ACLMessage");
    replyButton.setIcon(replyIcon);
    replyButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          replyButtonActionPerformed(e);
        }
      });
    viewButton.setBorder(border1);
    viewButton.setToolTipText("Show Selected ACLMessage");
    viewButton.setIcon(viewIcon);
    viewButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          viewButtonActionPerformed(e);
        }
      });
    deleteButton.setBorder(border1);
    deleteButton.setToolTipText("Delete Current ACLMessage");
    deleteButton.setIcon(deleteIcon);
    deleteButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButtonActionPerformed(e);
        }
      });
    statisticsButton.setBorder(border1);
    statisticsButton.setToolTipText("Show Statistics");
    statisticsButton.setIcon(statisticsIcon);
    statisticsButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          statisticsButtonActionPerformed(e);
        }
      });
    quitButton.setBorder(border1);
    quitButton.setToolTipText("Quit");
    quitButton.setIcon(quitIcon);
    quitButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          quitButtonActionPerformed(e);
        }
      });
    leftPanel.setBackground(Color.white);
    rightPanel.setBackground(Color.white);
    newMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newMenuItemActionPerformed(e);
        }
      });
    newMenuItem.setText("New Message");
    newMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    newMenuItem.setActionCommand("load");
    newMenuItem.setMnemonic('N');
    newMenuItem.setBackground(Color.white);
    loadMenuItem.setBackground(Color.white);
    loadMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    loadMenuItem.setText("load message");
    loadMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          loadMenuItemActionPerformed(e);
        }
      });
    saveMenuItem.setBackground(Color.white);
    saveMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    saveMenuItem.setText("save message");
    saveMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveMenuItemActionPerformed(e);
        }
      });
    sendMenuItem.setBackground(Color.white);
    sendMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    sendMenuItem.setMnemonic('S');
    sendMenuItem.setText("Send Message");
    sendMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          sendMenuItemActionPerformed(e);
        }
      });
    templatesMenuItem.setBackground(Color.white);
    templatesMenuItem.setEnabled(false);
    templatesMenuItem.setFont(new java.awt.Font("Dialog", 3, 12));
    templatesMenuItem.setText("Templates:");
    traceMenu.setBackground(Color.white);
    traceMenu.setFont(new java.awt.Font("Dialog", 0, 12));
    traceMenu.setMnemonic('T');
    traceMenu.setText("Trace");
    claerQueueMenuItem.setBackground(Color.white);
    claerQueueMenuItem.setActionCommand("load");
    claerQueueMenuItem.setMnemonic('C');
    claerQueueMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    claerQueueMenuItem.setText("Clear Trace");
    claerQueueMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          claerQueueMenuItemActionPerformed(e);
        }
      });
    currentMenuItem.setBackground(Color.white);
    currentMenuItem.setActionCommand("load");
    currentMenuItem.setMnemonic('U');
    currentMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    currentMenuItem.setText("Use Current ACLMessage");
    currentMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          currentMenuItemActionPerformed(e);
        }
      });
    replyMenuItem.setBackground(Color.white);
    replyMenuItem.setActionCommand("load");
    replyMenuItem.setMnemonic('R');
    replyMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    replyMenuItem.setText("Reply To Current ACLMessage");
    replyMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          replyMenuItemActionPerformed(e);
        }
      });
    deleteMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteMenuItemActionPerformed(e);
        }
      });
    deleteMenuItem.setText("Delete Current ACLMessage");
    deleteMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    deleteMenuItem.setActionCommand("load");
    deleteMenuItem.setMnemonic('D');
    deleteMenuItem.setBackground(Color.white);
    statisticsMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          statisticsMenuItemActionPerformed(e);
        }
      });
    statisticsMenuItem.setText("Statistics...");
    statisticsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    statisticsMenuItem.setActionCommand("load");
    statisticsMenuItem.setMnemonic('S');
    statisticsMenuItem.setBackground(Color.white);
    loadMsgMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          loadMsgMenuItemActionPerformed(e);
        }
      });
    loadMsgMenuItem.setText("Open ACLMessage...");
    loadMsgMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    loadMsgMenuItem.setActionCommand("load");
    loadMsgMenuItem.setMnemonic('L');
    loadMsgMenuItem.setBackground(Color.white);
    saveMsgMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveMsgMenuItemActionPerformed(e);
        }
      });
    saveMsgMenuItem.setText("Save ACLMessage...");
    saveMsgMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    saveMsgMenuItem.setActionCommand("load");
    saveMsgMenuItem.setMnemonic('S');
    saveMsgMenuItem.setBackground(Color.white);
    loadQMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          loadQMenuItemActionPerformed(e);
        }
      });
    loadQMenuItem.setText("Open ACLMessage Trace...");
    loadQMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    loadQMenuItem.setActionCommand("load");
    loadQMenuItem.setMnemonic('O');
    loadQMenuItem.setBackground(Color.white);
    saveQMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveQMenuItemActionPerformed(e);
        }
      });
    saveQMenuItem.setText("Save ACLMessage Trace...");
    saveQMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    saveQMenuItem.setActionCommand("load");
    saveQMenuItem.setBackground(Color.white);
    systemButton.setBorder(border1);
    systemButton.setToolTipText("To System.out");
    systemButton.setIcon(systemIcon);
    systemButton.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          systemButtonActionPerformed(e);
        }
      });
    systemOutMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          systemOutMenuItemActionPerformed(e);
        }
      });
    systemOutMenuItem.setText("To System.out");
    systemOutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    systemOutMenuItem.setActionCommand("load");
    systemOutMenuItem.setMnemonic('L');
    systemOutMenuItem.setBackground(Color.white);
    currentToOutMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          currentToOutMenuItemActionPerformed(e);
        }
      });
    currentToOutMenuItem.setText("Current To System.out");
    currentToOutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    currentToOutMenuItem.setActionCommand("load");
    currentToOutMenuItem.setMnemonic('S');
    currentToOutMenuItem.setBackground(Color.white);
    behaviourMenu.setBackground(Color.white);
    behaviourMenu.setFont(new java.awt.Font("Dialog", 0, 12));
    behaviourMenu.setMnemonic('B');
    behaviourMenu.setText("Behaviour");
    pingRadioButtonMenuItem.setText("Ping Behaviour");
    pingRadioButtonMenuItem.setSelected(true);
    pingRadioButtonMenuItem.setToolTipText("Responses to ACLMessages containing Ping");
    pingRadioButtonMenuItem.setBackground(Color.white);
    pingRadioButtonMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    pingRadioButtonMenuItem.addChangeListener(
      new javax.swing.event.ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          pingRadioButtonMenuItemStateChanged(e);
        }
      });
    localPingMenuItem.addActionListener(
      new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          localPingMenuItemActionPerformed(e);
        }
      });
    localPingMenuItem.setText("Local Ping");
    localPingMenuItem.setMnemonic('L');
    localPingMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
    localPingMenuItem.setToolTipText("Template for Local Ping ACLMessage");
    localPingMenuItem.setBackground(Color.white);
    this.getContentPane().add(mainSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
      , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    mainSplitPane.add(leftPanel, JSplitPane.LEFT);
    leftPanel.add(messageToolBar, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
      , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    leftPanel.add(aclPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
      , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    messageToolBar.add(newButton, null);
    messageToolBar.add(sendButton, null);
    messageToolBar.add(openButton, null);
    messageToolBar.add(saveButton, null);
    mainSplitPane.add(rightPanel, JSplitPane.RIGHT);
    rightPanel.add(aclTreeToolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
      , GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    aclTreeToolBar.add(readQueueButton, null);
    aclTreeToolBar.add(writeQueueButton, null);
    aclTreeToolBar.add(currentButton, null);
    aclTreeToolBar.add(replyButton, null);
    aclTreeToolBar.add(viewButton, null);
    aclTreeToolBar.add(systemButton, null);
    aclTreeToolBar.add(deleteButton, null);
    aclTreeToolBar.add(statisticsButton, null);
    aclTreeToolBar.add(quitButton, null);

    rightPanel.add(aclTreePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
      , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    // mainSplitPane.add(aclPanel, JSplitPane.LEFT);
//    mainSplitPane.add(aclTreePanel, JSplitPane.RIGHT);

    itsMenuBar.add(fileMenu);
    itsMenuBar.add(messagesMenu);
    itsMenuBar.add(traceMenu);
    itsMenuBar.add(behaviourMenu);
    itsMenuBar.add(helpMenu);
    fileMenu.add(loadMsgMenuItem);
    fileMenu.add(saveMsgMenuItem);
    fileMenu.addSeparator();
    fileMenu.add(loadQMenuItem);
    fileMenu.add(saveQMenuItem);
    fileMenu.addSeparator();
    fileMenu.add(exitMenuItem);
    messagesMenu.add(newMenuItem);
    messagesMenu.add(sendMenuItem);
    messagesMenu.add(systemOutMenuItem);
    messagesMenu.addSeparator();
//    messagesMenu.add(saveMenuItem);
//    messagesMenu.add(loadMenuItem);
    messagesMenu.add(templatesMenuItem);
    messagesMenu.add(localPingMenuItem);
//    messagesMenu.addSeparator();
    messagesMenu.add(pingLausanneMenuItem);
    messagesMenu.add(helloWorldMenuItem);
    messagesMenu.addSeparator();
    messagesMenu.add(amsRegMenuItem);
    messagesMenu.add(amsDeregMenuItem);
    messagesMenu.add(amsSearchMenuItem);
    messagesMenu.addSeparator();
    messagesMenu.add(dfRegMenuItem);
    messagesMenu.add(dfDeregMenuItem);
    messagesMenu.add(dfSearchMenuItem);
    messagesMenu.addSeparator();
    helpMenu.add(aboutMenuItem);
    traceMenu.add(claerQueueMenuItem);
    traceMenu.addSeparator();
    traceMenu.add(currentMenuItem);
    traceMenu.add(replyMenuItem);
    traceMenu.add(deleteMenuItem);
    traceMenu.add(currentToOutMenuItem);
    traceMenu.addSeparator();
    traceMenu.add(statisticsMenuItem);
    behaviourMenu.add(pingRadioButtonMenuItem);
    mainSplitPane.setDividerLocation(200);
  }


  private class AboutFrame extends JWindow {

    public AboutFrame() {
      try {
        jbInit();
        this.setSize(400, 200);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width / 2 - this.getSize().width / 2,
          screenSize.height / 2 - this.getSize().height / 2);

      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }


    void logoLabelMouseClicked(MouseEvent e) {
      this.setVisible(false);
    }


    void jLabel3MousePressed(MouseEvent e) {
      this.setVisible(false);
    }


    void jLabel2MouseClicked(MouseEvent e) {
      this.setVisible(false);
    }


    void jLabel3MouseClicked(MouseEvent e) {
      this.setVisible(false);
    }


    void logoLabelMouseEntered(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    void logoLabelMouseExited(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    void jLabel3MouseEntered(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    void jLabel3MouseExited(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    void jLabel2MouseEntered(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }


    void jLabel2MouseExited(MouseEvent e) {
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    void logoLabelMousePressed(MouseEvent e) {

    }


    void logoLabelMouseReleased(MouseEvent e) {

    }


    void jLabel2MousePressed(MouseEvent e) {

    }


    void jLabel2MouseReleased(MouseEvent e) {

    }


    private void jbInit() throws Exception {
      // this.setClosable(true);
      //this.setOpaque(false);
      border1 = new TitledBorder(BorderFactory.createLineBorder(new Color(0, 0, 128), 1), "TestAgent");
      this.getContentPane().setBackground(Color.white);
      this.getContentPane().setLayout(gridBagLayout1);
      contentPanel.setLayout(gridBagLayout2);
      logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
      logoLabel.setIcon(acklinIcon);
      logoLabel.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            logoLabelMouseClicked(e);
          }


          public void mouseEntered(MouseEvent e) {
            logoLabelMouseEntered(e);
          }


          public void mouseExited(MouseEvent e) {
            logoLabelMouseExited(e);
          }
        });
      jLabel1.setText("donated by Acklin B.V. to the Jade project");
      jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
      jLabel2.setText("web: www.acklin.nl  |  email: info@acklin.nl");
      jLabel2.addMouseListener(
        new java.awt.event.MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            jLabel2MouseClicked(e);
          }


          public void mouseEntered(MouseEvent e) {
            jLabel2MouseEntered(e);
          }


          public void mouseExited(MouseEvent e) {
            jLabel2MouseExited(e);
          }
        });
      contentPanel.setBackground(Color.white);
      contentPanel.setFont(new java.awt.Font("Dialog", 0, 11));
      contentPanel.setBorder(border1);
      this.getContentPane().add(contentPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      contentPanel.add(logoLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
      contentPanel.add(jLabel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
      contentPanel.add(jLabel2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
        , GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
    }


    GridBagLayout gridBagLayout1 = new GridBagLayout();

    ImageIcon acklinIcon =
      new ImageIcon(getClass().getResource("images/acklinabout.gif"));
    JPanel contentPanel = new JPanel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    JLabel logoLabel = new JLabel();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    Border border1;

  }


  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JSplitPane mainSplitPane = new JSplitPane();
  JMenuBar itsMenuBar = new JMenuBar();
  JMenu fileMenu = new JMenu();
  JMenuItem exitMenuItem = new JMenuItem();
  JMenu messagesMenu = new JMenu();
  JMenuItem helloWorldMenuItem = new JMenuItem();
  JMenuItem amsRegMenuItem = new JMenuItem();
  JMenuItem amsDeregMenuItem = new JMenuItem();
  JMenuItem amsSearchMenuItem = new JMenuItem();
  JMenuItem pingLausanneMenuItem = new JMenuItem();
  JMenuItem dfSearchMenuItem = new JMenuItem();
  JMenuItem dfDeregMenuItem = new JMenuItem();
  JMenuItem dfRegMenuItem = new JMenuItem();

  JMenu helpMenu = new JMenu();
  JMenuItem aboutMenuItem = new JMenuItem();
  JPanel leftPanel = new JPanel();
  JPanel rightPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JToolBar aclTreeToolBar = new JToolBar();
  JButton writeQueueButton = new JButton();
  JButton readQueueButton = new JButton();
  JButton openButton = new JButton();
  JButton sendButton = new JButton();
  JButton newButton = new JButton();
  JButton saveButton = new JButton();
  JToolBar messageToolBar = new JToolBar();
  JButton currentButton = new JButton();
  JButton replyButton = new JButton();
  JButton viewButton = new JButton();
  JButton deleteButton = new JButton();
  JButton statisticsButton = new JButton();
  JButton quitButton = new JButton();
  JMenuItem newMenuItem = new JMenuItem();
  JMenuItem loadMenuItem = new JMenuItem();
  JMenuItem saveMenuItem = new JMenuItem();
  JMenuItem sendMenuItem = new JMenuItem();
  JMenuItem templatesMenuItem = new JMenuItem();
  JMenu traceMenu = new JMenu();
  JMenuItem claerQueueMenuItem = new JMenuItem();
  JMenuItem currentMenuItem = new JMenuItem();
  JMenuItem replyMenuItem = new JMenuItem();
  JMenuItem deleteMenuItem = new JMenuItem();
  JMenuItem statisticsMenuItem = new JMenuItem();
  JMenuItem loadMsgMenuItem = new JMenuItem();
  JMenuItem saveMsgMenuItem = new JMenuItem();
  JMenuItem loadQMenuItem = new JMenuItem();
  JMenuItem saveQMenuItem = new JMenuItem();
  JButton systemButton = new JButton();
  JMenuItem systemOutMenuItem = new JMenuItem();
  JMenuItem currentToOutMenuItem = new JMenuItem();
  JMenu behaviourMenu = new JMenu();
  JRadioButtonMenuItem pingRadioButtonMenuItem = new JRadioButtonMenuItem();
  JMenuItem localPingMenuItem = new JMenuItem();

  ImageIcon newIcon;
  ImageIcon openIcon;
  ImageIcon saveIcon;
  ImageIcon sendIcon;
  ImageIcon readQueueIcon;
  ImageIcon saveQueueIcon;
  ImageIcon currentIcon;
  ImageIcon replyIcon;
  ImageIcon viewIcon;
  ImageIcon deleteIcon;
  ImageIcon statisticsIcon;
  ImageIcon quitIcon;
  ImageIcon systemIcon;
  ACLPanel aclPanel;

  TestAgent agent;
  ACLTracePanel aclTreePanel;
  Border border1;

}
//  ***EOF***
