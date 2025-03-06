/*
 * Copyright (C) 2025 org.kds
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kds.GUI;

import com.kds.GUI.NotesEditorController;

import IniUtil.IniUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Defaults;

import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

import org.crosswire.jsword.book.OSISUtil;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;

import org.crosswire.common.config.Config;

import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;

import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import org.crosswire.jsword.util.ConverterFactory;

import java.io.IOException;
import java.io.File;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javax.xml.transform.TransformerException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

/**
 * @ingroup GUI
 * @brief Main Window Controller
 * @details 
 * This controller incorporates some of the previous properties out of the 
 * Crosswire BibleDesktop application.
 * 
 * @implements Initializable
 * @sa GPL License
 */
public class MainWindowController implements Initializable 
{
    public static final String BIBLE_PROTOCOL = "bible";
    public static final String DICTIONARY_PROTOCOL = "dict";
    public static final String GREEK_DEF_PROTOCOL = "gdef";
    public static final String HEBREW_DEF_PROTOCOL = "hdef";
    public static final String GREEK_MORPH_PROTOCOL = "gmorph";
    public static final String HEBREW_MORPH_PROTOCOL = "hmorph";
    public static final String COMMENTARY_PROTOCOL = "comment";
    // -----  BIBLE NAMES STRINGS ----- //
    // Old Testament //
    public static final String BOOK_NAME_GEN_FULL="Genesis";
    public static final String BOOK_NAME_GEN_SHORT="Gen";
    public static final String BOOK_NAME_GEN_ALT="";
    public static final String BOOK_NAME_EXO_FULL="Exodus";
    public static final String BOOK_NAME_EXO_SHORT="Exo";
    public static final String BOOK_NAME_EXO_ALT="";
    public static final String BOOK_NAME_LEV_FULL="Leviticus";
    public static final String BOOK_NAME_LEV_SHORT="Lev";
    public static final String BOOK_NAME_LEV_ALT="";
    public static final String BOOK_NAME_NUM_FULL="Numbers";
    public static final String BOOK_NAME_NUM_SHORT="Num";
    public static final String BOOK_NAME_NUM_ALT="";
    public static final String BOOK_NAME_DEUT_FULL="Deuteronomy";
    public static final String BOOK_NAME_DEUT_SHORT="Deu";
    public static final String BOOK_NAME_DEUT_ALT="du";
    public static final String BOOK_NAME_JOSH_FULL="Joshua";
    public static final String BOOK_NAME_JOSH_SHORT="Jos";
    public static final String BOOK_NAME_JOSH_ALT="";
    public static final String BOOK_NAME_JUDG_FULL="Judges";
    public static final String BOOK_NAME_JUDG_SHORT="Judg";
    public static final String BOOK_NAME_JUDG_ALT="jdg,jud";
    public static final String BOOK_NAME_RUTH_FULL="RUTH";
    public static final String BOOK_NAME_RUTH_SHORT="RUT";
    public static final String BOOK_NAME_RUTH_ALT="rth";
    public static final String BOOK_NAME_1SAM_FULL="1 Samuel";
    public static final String BOOK_NAME_1SAM_SHORT="1Sa";
    public static final String BOOK_NAME_1SAM_ALT="isam";
    public static final String BOOK_NAME_2SAM_FULL="2 Samuel";
    public static final String BOOK_NAME_2SAM_SHORT="2Sa";
    public static final String BOOK_NAME_2SAM_ALT="iisam";
    public static final String BOOK_NAME_1KGS_FULL="1 Kings";
    public static final String BOOK_NAME_1KGS_SHORT="1Ki";
    public static final String BOOK_NAME_1KGS_ALT="1kgs,iki,ikg";
    public static final String BOOK_NAME_2KGS_FULL="2 Kings";
    public static final String BOOK_NAME_2KGS_SHORT="2Ki";
    public static final String BOOK_NAME_2KGS_ALT="12kgs,iiki,iikg";
    public static final String BOOK_NAME_1CHR_FULL="1 Chronicles";
    public static final String BOOK_NAME_1CHR_SHORT="1Ch";
    public static final String BOOK_NAME_1CHR_ALT="ich";
    public static final String BOOK_NAME_2CHR_FULL="2 Chronicles";
    public static final String BOOK_NAME_2CHR_SHORT="2Ch";
    public static final String BOOK_NAME_2CHR_ALT="iich";
    public static final String BOOK_NAME_EZRA_FULL="Ezra";
    public static final String BOOK_NAME_EZRA_SHORT="Ezr";
    public static final String BOOK_NAME_EZRA_ALT="";
    public static final String BOOK_NAME_NEH_FULL="Nehemiah";
    public static final String BOOK_NAME_NEH_SHORT="Neh";
    public static final String BOOK_NAME_NEH_ALT="";
    public static final String BOOK_NAME_ESTH_FULL="Esther";
    public static final String BOOK_NAME_ESTH_SHORT="Est";
    public static final String BOOK_NAME_ESTH_ALT="";
    public static final String BOOK_NAME_JOB_FULL="Job";
    public static final String BOOK_NAME_JOB_SHORT="Job";
    public static final String BOOK_NAME_JOB_ALT="";
    public static final String BOOK_NAME_PS_FULL="Psalms";
    public static final String BOOK_NAME_PS_SHORT="Psa";
    public static final String BOOK_NAME_PS_ALT="pss,psm";
    public static final String BOOK_NAME_PROV_FULL="Proverbs";
    public static final String BOOK_NAME_PROV_SHORT="Proverbs";
    public static final String BOOK_NAME_PROV_ALT="Pro";
    public static final String BOOK_NAME_ECCL_FULL="Ecclesiastes";
    public static final String BOOK_NAME_ECCL_SHORT="Ecc";
    public static final String BOOK_NAME_ECCL_ALT="qoh";
    public static final String BOOK_NAME_SONG_FULL="Song of Solomon";
    public static final String BOOK_NAME_SONG_SHORT="Song";
    public static final String BOOK_NAME_SONG_ALT="ss,songofsongs,sos,canticleofcanticles,canticle,can,coc";
    public static final String BOOK_NAME_ISA_FULL="Isaiah";
    public static final String BOOK_NAME_ISA_SHORT="Isa";
    public static final String BOOK_NAME_ISA_ALT="is";
    public static final String BOOK_NAME_JER_FULL="Jeremiah";
    public static final String BOOK_NAME_JER_SHORT="Jer";
    public static final String BOOK_NAME_JER_ALT="";
    public static final String BOOK_NAME_LAM_FULL="Lamentations";
    public static final String BOOK_NAME_LAM_SHORT="Lam";
    public static final String BOOK_NAME_LAM_ALT="";
    public static final String BOOK_NAME_EZEK_FULL="Ezekiel";
    public static final String BOOK_NAME_EZEK_SHORT="Eze";
    public static final String BOOK_NAME_EZEK_ALT="";
    public static final String BOOK_NAME_DAN_FULL="Daniel";
    public static final String BOOK_NAME_DAN_SHORT="Dan";
    public static final String BOOK_NAME_DAN_ALT="";
    public static final String BOOK_NAME_HOS_FULL="Hosea";
    public static final String BOOK_NAME_HOS_SHORT="Hos";
    public static final String BOOK_NAME_HOS_ALT="";
    public static final String BOOK_NAME_JOEL_FULL="Joel";
    public static final String BOOK_NAME_JOEL_SHORT="Joe";
    public static final String BOOK_NAME_JOEL_ALT="";
    public static final String BOOK_NAME_AMOS_FULL="Amos";
    public static final String BOOK_NAME_AMOS_SHORT="Amo";
    public static final String BOOK_NAME_AMOS_ALT="";
    public static final String BOOK_NAME_OBAD_FULL="Obadiah";
    public static final String BOOK_NAME_OBAD_SHORT="Obd";
    public static final String BOOK_NAME_OBAD_ALT="";
    public static final String BOOK_NAME_JONAH_FULL="Jonah";
    public static final String BOOK_NAME_JONAH_SHORT="Jon";
    public static final String BOOK_NAME_JONAH_ALT="jnh";
    public static final String BOOK_NAME_MIC_FULL="Micah";
    public static final String BOOK_NAME_MIC_SHORT="Mic";
    public static final String BOOK_NAME_MIC_ALT="";
    public static final String BOOK_NAME_NAH_FULL="Nahum";
    public static final String BOOK_NAME_NAH_SHORT="Nah";
    public static final String BOOK_NAME_NAH_ALT="";
    public static final String BOOK_NAME_HAB_FULL="Habakkuk";
    public static final String BOOK_NAME_HAB_SHORT="Hab";
    public static final String BOOK_NAME_HAB_ALT="";
    public static final String BOOK_NAME_ZEPH_FULL="Zephaniah";
    public static final String BOOK_NAME_ZEPH_SHORT="Zep";
    public static final String BOOK_NAME_ZEPH_ALT="";
    public static final String BOOK_NAME_HAG_FULL="Haggai";
    public static final String BOOK_NAME_HAG_SHORT="Hag";
    public static final String BOOK_NAME_HAG_ALT="";
    public static final String BOOK_NAME_ZECH_FULL="Zechariah";
    public static final String BOOK_NAME_ZECH_SHORT="Zec";
    public static final String BOOK_NAME_ZECH_ALT="";
    public static final String BOOK_NAME_MAL_FULL="Malachi";
    public static final String BOOK_NAME_MAL_SHORT="Mal";
    public static final String BOOK_NAME_MAL_ALT="";
    // New Testament //
    public static final String BOOK_NAME_MATT_FULL="Matthew";
    public static final String BOOK_NAME_MATT_SHORT="Mat";
    public static final String BOOK_NAME_MATT_ALT="mt";
    public static final String BOOK_NAME_MARK_FULL="Mark";
    public static final String BOOK_NAME_MARK_SHORT="Mar";
    public static final String BOOK_NAME_MARK_ALT="mk,mrk";
    public static final String BOOK_NAME_LUKE_FULL="Luke";
    public static final String BOOK_NAME_LUKE_SHORT="Luk";
    public static final String BOOK_NAME_LUKE_ALT="lk";
    public static final String BOOK_NAME_JOHN_FULL="John";
    public static final String BOOK_NAME_JOHN_SHORT="Joh";
    public static final String BOOK_NAME_JOHN_ALT="jn,jhn";
    public static final String BOOK_NAME_ACTS_FULL="Acts";
    public static final String BOOK_NAME_ACTS_SHORT="Act";
    public static final String BOOK_NAME_ACTS_ALT="";
    public static final String BOOK_NAME_ROM_FULL="Romans";
    public static final String BOOK_NAME_ROM_SHORT="Rom";
    public static final String BOOK_NAME_ROM_ALT="";
    public static final String BOOK_NAME_1COR_FULL="1 Corinthians";
    public static final String BOOK_NAME_1COR_SHORT="1Cor";
    public static final String BOOK_NAME_1COR_ALT="ico";
    public static final String BOOK_NAME_2COR_FULL="2 Corinthians";
    public static final String BOOK_NAME_2COR_SHORT="2Cor";
    public static final String BOOK_NAME_2COR_ALT="iico";
    public static final String BOOK_NAME_GAL_FULL="Galatians";
    public static final String BOOK_NAME_GAL_SHORT="Gal";
    public static final String BOOK_NAME_GAL_ALT="";
    public static final String BOOK_NAME_EPH_FULL="Ephesians";
    public static final String BOOK_NAME_EPH_SHORT="Eph";
    public static final String BOOK_NAME_EPH_ALT="";
    public static final String BOOK_NAME_PHIL_FULL="Philippians";
    public static final String BOOK_NAME_PHIL_SHORT="Phili";
    public static final String BOOK_NAME_PHIL_ALT="php";
    public static final String BOOK_NAME_COL_FULL="Colossians";
    public static final String BOOK_NAME_COL_SHORT="Col";
    public static final String BOOK_NAME_COL_ALT="co";
    public static final String BOOK_NAME_1THESS_FULL="1 Thessalonians";
    public static final String BOOK_NAME_1THESS_SHORT="1Th";
    public static final String BOOK_NAME_1THESS_ALT="ith";
    public static final String BOOK_NAME_2THESS_FULL="2 Thessalonians";
    public static final String BOOK_NAME_2THESS_SHORT="2Th";
    public static final String BOOK_NAME_2THESS_ALT="iith";
    public static final String BOOK_NAME_1TIM_FULL="1 Timothy";
    public static final String BOOK_NAME_1TIM_SHORT="1Ti";
    public static final String BOOK_NAME_1TIM_ALT="1tm,iti,itm";
    public static final String BOOK_NAME_2TIM_FULL="2 Timothy";
    public static final String BOOK_NAME_2TIM_SHORT="2Ti";
    public static final String BOOK_NAME_2TIM_ALT="2tm,iiti,iitm";
    public static final String BOOK_NAME_TITUS_FULL="Titus";
    public static final String BOOK_NAME_TITUS_SHORT="Tit";
    public static final String BOOK_NAME_TITUS_ALT="";
    public static final String BOOK_NAME_PHLM_FULL="Philemon";
    public static final String BOOK_NAME_PHLM_SHORT="Phile";
    public static final String BOOK_NAME_PHLM_ALT="phm,phlm";
    public static final String BOOK_NAME_HEB_FULL="Hebrews";
    public static final String BOOK_NAME_HEB_SHORT="Heb";
    public static final String BOOK_NAME_HEB_ALT="";
    public static final String BOOK_NAME_JAS_FULL="James";
    public static final String BOOK_NAME_JAS_SHORT="Jam";
    public static final String BOOK_NAME_JAS_ALT="jas";
    public static final String BOOK_NAME_1PET_FULL="1 Peter";
    public static final String BOOK_NAME_1PET_SHORT="1Pe";
    public static final String BOOK_NAME_1PET_ALT="1ptr,ip";
    public static final String BOOK_NAME_2PET_FULL="2 Peter";
    public static final String BOOK_NAME_2PET_SHORT="2Pe";
    public static final String BOOK_NAME_2PET_ALT="2ptr,iip";
    public static final String BOOK_NAME_1JOHN_FULL="1 John";
    public static final String BOOK_NAME_1JOHN_SHORT="1Jo";
    public static final String BOOK_NAME_1JOHN_ALT="1jn,1jh,ijo,ihn,ijh";
    public static final String BOOK_NAME_2JOHN_FULL="2 John";
    public static final String BOOK_NAME_2JOHN_SHORT="2Jo";
    public static final String BOOK_NAME_2JOHN_ALT="2jn,2jh,iijo,iihn,iijh";
    public static final String BOOK_NAME_3JOHN_FULL="3 John";
    public static final String BOOK_NAME_3JOHN_SHORT="3Jo";
    public static final String BOOK_NAME_3JOHN_ALT="3jn,3jh,iiijo,iiihn,iiijh";
    public static final String BOOK_NAME_JUDE_FULL="Jude";
    public static final String BOOK_NAME_JUDE_SHORT="Jude";
    public static final String BOOK_NAME_JUDE_ALT="";
    public static final String BOOK_NAME_REV_FULL="Revelation of John";
    public static final String BOOK_NAME_REV_SHORT="Rev";
    public static final String BOOK_NAME_REV_ALT="rv,apocalypse";
    // Apocrypha //
    public static final String BOOK_NAME_TOB_FULL="Tobit";
    public static final String BOOK_NAME_TOB_SHORT="Tob";
    public static final String BOOK_NAME_TOB_ALT="";
    public static final String BOOK_NAME_JDT_FULL="Judith";
    public static final String BOOK_NAME_JDT_SHORT="Jdt";
    public static final String BOOK_NAME_JDT_ALT="";
    public static final String BOOK_NAME_ADDESTH_FULL="Additions to Esther";
    public static final String BOOK_NAME_ADDESTH_SHOT="Add Est";
    public static final String BOOK_NAME_ADDESTH_ALT="";
    public static final String BOOK_NAME_WIS_FULL="Wisdom of Solomon";
    public static final String BOOK_NAME_WIA_SHORT="Wis";
    public static final String BOOK_NAME_WIS_ALT="";
    public static final String BOOK_NAME_SIR_FULL="Sirach";
    public static final String BOOK_NAME_SIR_SHORT="Sir";
    public static final String BOOK_NAME_SIR_ALT="Ecclesiasticus";
    public static final String BOOK_NAME_BAR_FULL="Baruch";
    public static final String BOOK_NAME_BAR_SHORT="Bar";
    public static final String BOOK_NAME_BAR_ALT="";
    public static final String BOOK_NAME_EPJER_FULL="Epistle of Jeremiah";
    public static final String BOOK_NAME_EPJER_SHORT="Ep Jer";
    public static final String BOOK_NAME_EPJER_ALT="letterofjeremiah,letterjeremiah,epistlejeremiah,epjeremiah";
    public static final String BOOK_NAME_PRAZR_FULL="Prayer of Azariah";
    public static final String BOOK_NAME_PRAZR_SHORT="Pr Azar";
    public static final String BOOK_NAME_PRAZR_ALT="prayerazariah,prayazariah,songofthreechildren,songthreechildren,songof3children,song3children";
    public static final String BOOK_NAME_SUS_FULL="Susanna";
    public static final String BOOK_NAME_SUS_SHORT="Sus";
    public static final String BOOK_NAME_SUS_ALT="";
    public static final String BOOK_NAME_BEL_FULL="Bel and the Dragon";
    public static final String BOOK_NAME_BEL_SHORT="Bel";
    public static final String BOOK_NAME_BEL_AKT="beldragon,belanddragon";
    public static final String BOOK_NAME_1MACC_FULL="1 Maccabees";
    public static final String BOOK_NAME_1MACC_SHORT="1 Macc";
    public static final String BOOK_NAME_1MACC_ALT="imaccabees";
    public static final String BOOK_NAME_2MACC_FULL="2 Maccabees";
    public static final String BOOK_NAME_2MACC_SHORT="2 Macc";
    public static final String BOOK_NAME_2MACC_ALT="iimaccabees";
    public static final String BOOK_NAME_3MACC_FULL="3 Maccabees";
    public static final String BOOK_NAME_3MACC_SHORT="3 Macc";
    public static final String BOOK_NAME_3MACC_ALT="iiimaccabees";
    public static final String BOOK_NAME_4MACC_FULL="4 Maccabees";
    public static final String BOOK_NAME_4MACC_SHORT="4 Macc";
    public static final String BOOK_NAME_4MACC_ALT="ivmaccabees";
    public static final String BOOK_NAME_PRMAN_FULL="Prayer of Manasseh";
    public static final String BOOK_NAME_PRMAN_SHORT="Prayer of Manasseh";
    public static final String BOOK_NAME_PRMAN_ALT="prayermanasseh,praymanasseh,prmanasseh";
    public static final String BOOK_NAME_1ESD_FULL="1 Esd";
    public static final String BOOK_NAME_1ESD_SHORT="1 Esdras";
    public static final String BOOK_NAME_1ESD_ALT="iesdras";
    public static final String BOOK_NAME_2ESD_FULL="2 Esd";
    public static final String BOOK_NAME_2ESD_SHORT="2 Esdras";
    public static final String BOOK_NAME_2ESD_ALT="iiesdras";
    public static final String BOOK_NAME_PS151_FULL="Psalm 151";
    public static final String BOOK_NAME_PS151_SHORT="Ps151";
    public static final String BOOK_NAME_PS151_ALT="pss151,psm151";
    // Rahlfs' LXX  //
    public static final String BOOK_NAME_ODES_FULL="Odes";
    public static final String BOOK_NAME_ODES_SHORT="";
    public static final String BOOK_NAME_ODES_ALT="";
    public static final String BOOK_NAME_PSSSOL_FULL="Psalms of Solomon";
    public static final String BOOK_NAME_PSSSOL_SHORT="Ps Sol";
    public static final String BOOK_NAME_PSSSOL_ALT="psssolomon,psmsolomon";
    // Vulgate & other later Latin mss //
    public static final String BOOK_NAME_EPLOA_FULL="Epistle to the Laodiceans";
    public static final String BOOK_NAME_EPLOA_SHORT="Ep Lao";
    public static final String BOOK_NAME_EPLOA_ALT="eplaodiceans";
    public static final String BOOK_NAME_3ESD_FULL="3 Esdras";
    public static final String BOOK_NAME_3ESD_SHORT="3 Esd";
    public static final String BOOK_NAME_3ESD_ALT="iiiesdras";
    public static final String BOOK_NAME_4ESD_FULL="4 Esdras";
    public static final String BOOK_NAME_4ESD_SHORT="4 Esd";
    public static final String BOOK_NAME_4ESD_ALT="ivesdras";
    public static final String BOOK_NAME_5ESD_FULL="5 Esdras";
    public static final String BOOK_NAME_5ESD_SHORT="5 Esd";
    public static final String BOOK_NAME_5ESD_ALT="vesdras";
    // Ethiopian Orthodox Canon/Ge'ez Translation //
    public static final String BOOK_NAME_1EN_FULL="1 Enoch";
    public static final String BOOK_NAME_1EN_SHORT="1 En";
    public static final String BOOK_NAME_1EN_ALT="ienoch";
    public static final String BOOK_NAME_JUB_FULL="Jubilees";
    public static final String BOOK_NAME_JUB_SHORT="Jub";
    public static final String BOOK_NAME_JUB_ALT="";
    public static final String BOOK_NAME_4BAR_FULL="4 Baruch";
    public static final String BOOK_NAME_4BAR_SHORT="4 Bar";
    public static final String BOOK_NAME_4BAR_ALT="";
    public static final String BOOK_NAME_ASCENISA_FULL="Vision of Isaiah";
    public static final String BOOK_NAME_ASCENISA_SHORT="AscenIsa";
    public static final String BOOK_NAME_ASCENISA_ALT="";
    public static final String BOOK_NAME_PSJOS_FULL="Pseudo Josephus";
    public static final String BOOK_NAME_PSJOS_SHORT="Ps Jos";
    public static final String BOOK_NAME_PSJOS_ALT="";
    //  Coptic Orthodox Canon  //
    public static final String BOOK_NAME_APOSCON_FULL="Apostolic Constitutions and Canons";
    public static final String BOOK_NAME_APOSCON_SHORT="Apos Con";
    public static final String BOOK_NAME_APOSCON_ALT="";
    public static final String BOOK_NAME_1CLEMM_FULL="1 Clement";
    public static final String BOOK_NAME_1CLEMM_SHORT="1Clem";
    public static final String BOOK_NAME_1CLEMM_ALT="iclement";
    public static final String BOOK_NAME_2CLEMM_FULL="2 Clement";
    public static final String BOOK_NAME_2CLEMM_SHORT="2Clem";
    public static final String BOOK_NAME_2CLEMM_ALT="iiclement";
    // Armenian Orthodox Canon  //
    public static final String BOOK_NAME_3COR_FULL="3 Corinthians";
    public static final String BOOK_NAME_3COR_SHORT="3Cor";
    public static final String BOOK_NAME_3COR_ALT="";
    public static final String BOOK_NAME_EPCORPAUL_FULL="Epistle of the Corinthians to Paul and His Response";
    public static final String BOOK_NAME_EPCORPAUL_SHORT="Ep Cor Paul";
    public static final String BOOK_NAME_EPCORPAUL_ALT="";
    public static final String BOOK_NAME_JOSASEN_FULL="Joseph and Asenath";
    public static final String BOOK_NAME_JOSASEN_SHORT="Jos Asen";
    public static final String BOOK_NAME_JOSASEN_ALT="";
    public static final String BOOK_NAME_T12PATR_FULL="Testaments of the Twelve Patriarchs";
    public static final String BOOK_NAME_T12PATR_SHORT="T12Patr";
    public static final String BOOK_NAME_T12PATR_ALT="";
    public static final String BOOK_NAME_T12PATRTASH_FULL="Testaments of Asher";
    public static final String BOOK_NAME_T12PATRTASH_SHORT="TAsh";
    public static final String BOOK_NAME_T12PATRASH_ALT="";
    public static final String BOOK_NAME_T12PATRBENJ_FULL="Testaments of Benjamin";
    public static final String BOOK_NAME_T12PATRBENJ_SHORT="TBenj";
    public static final String BOOK_NAME_T12PATRBENJ_ALT="";
    public static final String BOOK_NAME_T12PATRDAN_FULL="Testaments of Dan";
    public static final String BOOK_NAME_T12PATRDAN_SHORT="TDan";
    public static final String BOOK_NAME_T12PATRDAN_ALT="";
    public static final String BOOK_NAME_T12PATRGAD_FULL="Testaments of Gad";
    public static final String BOOK_NAME_T12PATRGAD_SHORT="TGad";
    public static final String BOOK_NAME_T12PATRGAD_ALT="";
    public static final String BOOK_NAME_T12PATRTISS_FULL="Testaments of Issachar";
    public static final String BOOK_NAME_T12PATRTISS_SHORT="TIss";
    public static final String BOOK_NAME_T12PATRTISS_ALT="";
    public static final String BOOK_NAME_T12PATRJOS_FULL="Testaments of Joseph";
    public static final String BOOK_NAME_T12PATRJOS_SHORT="TJos";
    public static final String BOOK_NAME_T12PATRJOS_ALT="";
    public static final String BOOK_NAME_T12PATRJUD_FULL="Testaments of Judah";
    public static final String BOOK_NAME_T12PATRJUD_SHORT="TJud";
    public static final String BOOK_NAME_T12PATRJUD_ALT="";
    public static final String BOOK_NAME_T12PATRLEVI_FULL="Testaments of Levi";
    public static final String BOOK_NAME_T12PATRLEVI_SHORT="TLevi";
    public static final String BOOK_NAME_T12PATRLEVI_ALT="";
    public static final String BOOK_NAME_T12PATRNAPH_FULL="Testaments of Naphtali";
    public static final String BOOK_NAME_T12PATRNAPH_SHORT="TNaph";
    public static final String BOOK_NAME_T12PATRNAPH_ALT="";
    public static final String BOOK_NAME_T12PATRTREU_FULL="Testaments of Reuben";
    public static final String BOOK_NAME_T12PATRTRUE_SHORT="TReu";
    public static final String BOOK_NAME_T12PATRTREU_ALT="";
    public static final String BOOK_NAME_T12PATRTSim_FULL="Testaments of Simeon";
    public static final String BOOK_NAME_T12PATRTSim_SHORT="TSim";
    public static final String BOOK_NAME_T12PATRTSim_ALT="";
    public static final String BOOK_NAME_T12PATRTZEB_FULL="Testaments of Zebulun";
    public static final String BOOK_NAME_T12PATRTZEB_SHORT="TZeb";
    public static final String BOOK_NAME_T12PATRTZEB_ALT="";
    // Peshitta  //
    public static final String BOOK_NAME_2BAR_FULL="2 Baruch";
    public static final String BOOK_NAME_2BAR_SHORT="2 Bar";
    public static final String BOOK_NAME_2BAR_ALT="";
    public static final String BOOK_NAME_EPBAR_FULL="Epistile of Baruch";
    public static final String BOOK_NAME_EPBAR_SHORT="EpBar";
    public static final String BOOK_NAME_EPBAR_ALT="";
    // Codex Sinaiticus  //
    public static final String BOOK_NAME_BARN_FULL="Barnabas";
    public static final String BOOK_NAME_BARN_SHORT="Barn";
    public static final String BOOK_NAME_BARN_ALT="";
    public static final String BOOK_NAME_HERM_FULL="Shepherd of Hermas";
    public static final String BOOK_NAME_HERM_SHORT="Herm";
    public static final String BOOK_NAME_HERM_ALT="";
    public static final String BOOK_NAME_HERMMAND_FULL="Mandates";
    public static final String BOOK_NAME_HERMMAND_SHORT="Mand";
    public static final String BOOK_NAME_HERMMAND_ALT="";
    public static final String BOOK_NAME_HERMSIM_FULL="Similitudes";
    public static final String BOOK_NAME_HERMSIM_SHORT="Sim";
    public static final String BOOK_NAME_HERMSIM_ALT="";
    public static final String BOOK_NAME_HERMVIS_FULL="Visions";
    public static final String BOOK_NAME_HERMVIS_SHORT="Vis";
    public static final String BOOK_NAME_HERMVIS_ALT="";
    // Other non-canonical books  //
    public static final String BOOK_NAME_ADDDAN_FULL="Additions to Daniel";
    public static final String BOOK_NAME_ADDDAN_SHORT="AddDan";
    public static final String BOOK_NAME_ADDDAN_ALT="";
    public static final String BOOK_NAME_ADDPS_FULL="Additions to Psalms";
    public static final String BOOK_NAME_ADDPS_SHORT="AddPs";
    public static final String BOOK_NAME_ADDPA_ALT="";
    public static final String BOOK_NAME_ESTHGR_FULL="Esther (Greek)";
    public static final String BOOK_NAME_ESTHGR_SHORT="EsthGr";
    public static final String BOOK_NAME_ESTHGR_ALT="";
    // Introduction titles for the book as a whole, the OT and the NT. //
    public static final String BOOK_NAME_INTROBIBLE_FULL="Bible Introduction";
    public static final String BOOK_NAME_INTROBIBLE_SHORT="Bible Intro";
    public static final String BOOK_NAME_INTROBIBLE_ALT="";
    public static final String BOOK_NAME_INTROOT_FULL="Old Testament Introduction";
    public static final String BOOK_NAME_INTROOT_SHORT="OT Intro";
    public static final String BOOK_NAME_INTROOT_ALT="";
    public static final String BOOK_NAME_INTRONT_FULL="New Testament Introduction";
    public static final String BOOK_NAME_INTRONT_SHORT="NT Intro";
    public static final String BOOK_NAME_INTRONT_ALT="";
    // ---- END BIBLE NAMES STRINGS ---- //

    protected FXMLLoader loader;

    protected Parent root;
    protected Screen screen;
    protected Stage  thisstage;
    protected Scene  thisscene;

    protected Key key;

    // ================================= //
    // FXML DEFINITIONS                  //
    // ================================= //
    @FXML private VBox MainWindow;

    @FXML private SplitPane  splitMultiBookPane;
    @FXML private TabPane    tabPaneVersions;
    @FXML private AnchorPane paneTreeView;
    @FXML private AnchorPane paneTabView;
    
    
    @FXML private TreeView<Object> treeLibraryView;

    @FXML private MenuBar  menuBar;
    @FXML private Menu     menuFile;
    @FXML private Menu     menuViewVerseNumbers;
    @FXML private Menu     menuSettingsThemes;
    @FXML private Menu     menuBibles;
    @FXML private Menu     menuHelp;
    @FXML private MenuItem menuFileNewView;
    @FXML private MenuItem menuFileCloseCurrent;
    @FXML private MenuItem menuFileCloseOther;
    @FXML private MenuItem menuFileCloseAll;
    @FXML private MenuItem menuFileClearView;
    @FXML private MenuItem menuFileOpen;
    @FXML private MenuItem menuFileSave;
    @FXML private MenuItem menuFileSaveAs;
    @FXML private MenuItem menuFileSaveAll;
    @FXML private MenuItem menuFileClose;
    @FXML private MenuItem menuSettingsConfig;
    @FXML private MenuItem menuSettingsLibSetup;
    @FXML private MenuItem menuBiblesTrans;
    @FXML private MenuItem menuBiblesVersions;
    @FXML private MenuItem menuHelpAbout;
    @FXML private MenuItem menuHelpContents;
    @FXML private RadioMenuItem menuViewShowVerseNum;
    @FXML private RadioMenuItem menuViewShowChapVerse;
    @FXML private CheckMenuItem menuViewNotes;
    @FXML private RadioMenuItem menuSettingsSetNormalTheme;
    @FXML private RadioMenuItem menuSettingsSetDarkTheme;
    @FXML private RadioMenuItem menuSettingsSetMintTheme;
    @FXML private RadioMenuItem menuSettingsSetColorfulTheme;
    @FXML private RadioMenuItem menuSettingsSetFancyTheme;

    @FXML private ToolBar  toolBar;

    @FXML private Tab      tab1;

    @FXML private ChoiceBox<String> cboVersion;
    @FXML private ChoiceBox<String> cboBooks;
    @FXML private ChoiceBox<String> cboChapter;

    @FXML private TextField textSrch;
    @FXML private TextField textKey;

    @FXML private HBox     statusBar;    

    @FXML private Label    statusMsg1;
    @FXML private Label    statusMsg2;
    @FXML private Label    statusMsg3;
    @FXML private Label    locMsg1;
    
    @FXML private Button brnExit;
    @FXML private Button btnEnableSrch;
    @FXML private Button btnSrchFor;
    @FXML private Button btnSelectSrch;
    @FXML private Button btnGoSrch;
    @FXML private Button btnSubView;
    @FXML private Button btnAddView;
    @FXML private Button btnAdvSrch;
    @FXML private Button btnOpen;
    @FXML private Button btnSave;
    @FXML private Button btnCopy;
    @FXML private Button btnBack;
    @FXML private Button btnFwd;
    @FXML private Button btnHelpContents;
    @FXML private Button btnHelp;
    @FXML private Button btnSrchText;
    @FXML private Button btnSelSrchText;
   
    @FXML private ToggleGroup setStylesGroup;

    @FXML private WebView reference;
    // ================================= //
    // END FXML DEFINITIONS              //
    // ================================= //

    private transient BookDataDisplay display;
    private transient Config config;
    private transient Book work;

    private boolean debug_log;
    private boolean hasRefWorks;
    private boolean hasDictionaries;
    private boolean hasCommentaries;
    private boolean compareShowing;
    private boolean DefaultDictionary_Hidden;
    private boolean DefaultCommentary_Hidden;
    private boolean DefaultHebrewParse_Hidden;

    private int     activeTabCount;
    private int     activeChapt; // chapter being displayed

    private double uix;
    private double uiy;
    private double uiw;
    private double uih;

    private String RetainCurrent_Name;
    private String RetainCurrent_Help;
    private String DefaultBible_Name;
    private String DefaultBible_Help;
    private String DefaultDictionary_Name;
    private String DefaultDictionary_Help;
    private String DefaultCommentary_Name;
    private String DefaultCommentary_Help;
    private String DefaultDailyDevotional_Name;
    private String DefaultDailyDevotional_Help;
    private String DefaultGreekDefinitions_Name;
    private String DefaultGreekDefinitions_Help;
    private String DefaultHebrewDefinitions_Name;
    private String DefaultHebrewDefinitions_Help;
    private String DefaultGreekParse_Name;
    private String DefaultGreekParse_Help;
    private String DefaultHebrewParse_Name;
    private String DefaultHebrewParse_Help;

    private String version;
    private String versionsPath;
    private String strAppPath;
    private String defaultLang;
    private String default_download_path;
    private String selectedTheme;
    
    private String activePath;  // path to bibles
    private String activeText;  // name of book in the library
    private String activeBook;  // name of book in the bible
    private String activeKey;
    private String iniFilePath;
    
    private IniUtil    settings;

    private FileChooser fc;
    private DirectoryChooser dirs;
    private File choice;
    
    private ObservableList<String> translations;
    private ObservableList<String> versions;
    private ObservableList<String> chapters;
    private ObservableList<String> verses;
    private ObservableList<String> activeTabs;
    
    private List dictionaryKeyList;
    private List bookList;
    
    private ObservableList<TreeItem<CustomItem>> bibles;

    private ObservableMap<String, String> NamePair;

    private ImageView imgobj = new ImageView();
    private ImageView leafobj = new ImageView();

    private Image openimg = new Image(this.getClass().getResourceAsStream("/Images/openbook-48.png"));
    private Image leafimg = new Image(this.getClass().getResourceAsStream("/Images/book-48_orange.png"));
    
    private Parent maincont;
    private MainWindowController MWC;
    private CustomItem item;

    private transient Book[] selected;

    private static boolean sidebarShowing;
    private static boolean viewSourceShowing;

    private static LoggerContext lc;

    private static final String THISMODULE  = "MainWindowController";
    private static final String MAINSECTION = "MAIN";
    private static final String LIBSECTION  = "LIBRARIES";
    private static final String BIBLESSECTION = "BIBLES";
    private static final String TITLE = "BibleView Main Window ";
    private static final String EMPTY_STRING = "";
    private static final String CONFIG_KEY = "config";
    private static final String DESKTOP_KEY = "desktop";
    private static final String CONV_KEY = "converters";
    private static final String CSWING_KEY = "cswing-styles";
    private static final String BIBLE_NAME = "KJV";

    private static enum Mode { CLEAR, PASSAGE, SEARCH; }
    private static Mode mode;

    private static final Logger lgr = LoggerFactory.getLogger(MainWindowController.class);

    /**
     * @brief Initializes the controller class.
     * 
     * @param url URL
     * @param rb  ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        initLocalVars();
        readSettings();
        initLocalObjects();
    }
  
    // ========================================== //
    // PUBLIC METHODS                             //
    // ========================================== //
    public void deselectNotes()
    {
        menuViewNotes.setSelected(false);
        thisscene.setCursor(Cursor.DEFAULT);
    }

    public void setStage(Stage mstage) 
    {
        try
        {
            thisstage = mstage;
        }
        catch ( NullPointerException e )
        {
            String msg = "setStage(): NullPointerException stage not set="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
    }
    
    public void setScene(Scene mscene) 
    {
        activeTabs = FXCollections.observableArrayList();

        try
        {
            thisscene = mscene;
            thisstage.setScene(mscene);
            thisscene.setCursor(Cursor.WAIT);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }

        thisstage.setTitle(TITLE + version);

        try
        {
            fillBibleChoiceBox();
            fillBooksChoiceBox();
            fillChapterChoiceBox();
            loadVerseArray();
        }
        catch ( Exception e )
        {
            String msg = "Error filling combo boxes and array="+e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        selectFolder();
        try
        {
            getNodesForDirectory(choice.toString());
        }
        catch ( Exception e )
        {
            String msg = "Error geting nodes in library folders =" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        thisstage.show();
        displaySettings();

        doStyling(selectedTheme);

        thisscene.setCursor(Cursor.DEFAULT);
        // initialize for display
        textSrch.setText("Genesis 1");
        statusMsg1.setText("READY . . .");
        statusMsg2.setText("");
        statusMsg3.setText("");
        activeTabs.addAll("KJV");
        activeTabCount++;
        activeText = "kjv";
        activeBook = "Gen";
        activeChapt = 1;
        locMsg1.setText("Genesis 1");
        tab1.setText("KJV");
        generateConfig();

        BooksListener bl = new BooksListener() 
        {
            public void bookAdded(BooksEvent ev) 
            {
                //Desktop.this.generateConfig();
            }
        
            public void bookRemoved(BooksEvent ev) 
            {
               //Desktop.this.generateConfig();
            }
        };

        Books.installed().addBooksListener(bl);

        try 
        {
            installWork();
        }
        catch ( Exception e ) 
        {
            String msg = e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        try
        {
            displayDefaultText();
        }
        catch ( Exception e ) 
        {
            String msg = e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        
        try
        {
            readDictionary();
            search();
            rankedSearch();
        }
        catch ( BookException e )
        {
            String msg = "Error finding search text - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        
        try
        {
            searchAndShow();
        }
        catch ( BookException | SAXException e )
        {
            String msg = "Error showing search text - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
    }

    /**
     * This should be set after the configuration values are saved.
     * @param trans 
     */
    public void setTranslations(ObservableList<String> trans)
    {
        translations = trans;
    }

    /**
     * This should be set after the configuration values are saved.
     * @param ver 
     */
    public void setVersions(ObservableList<String> ver)
    {
        versions = ver;
    }

    // =============================  //
    // ACTION HANDLERS                //
    // =============================  //
    @FXML private void NewView(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void CloseCurrentVew(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void ClearCurrentView(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void CloseOtherVews(ActionEvent event) 
    {
        // TODO:
    }

    @FXML
    private void CloseAllViews(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void Open(ActionEvent event) 
    {
        checkForBooks();
    }

    @FXML private void Save(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void SaveAs(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void SaveAll(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void OpenView(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void Copy(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void EnableSrch(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void showAdvanced(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void SearchFor(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void ShowPassage(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void Search(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void SubtractView(ActionEvent event) 
    {
        // TODO:
    }

    @FXML private void AddView(ActionEvent event) 
    {
        // TODO:
    }


    @FXML
    private void actionSrchText(ActionEvent event) 
    {
        // TODO:
    }

    @FXML
    private void actionSelSrchText(ActionEvent event) 
    {
        // TODO:
    }
    
    @FXML private void actionSetNormalStyle()   { doStyling("Normal"); }

    @FXML private void actionSetDarkStyle()     { doStyling("Dark"); }

    @FXML private void actionSetMintStyle()     { doStyling("Mint"); }

    @FXML private void actionSetColorfulStyle() { doStyling("Colorful"); }

    @FXML private void actionSetFancyStyle()    { doStyling("Fancy"); }

    @FXML private void actionShowNotes(ActionEvent event) 
    {
        Stage notesstage;
        Scene notesscene;
        Parent notesroot;
        NotesEditorController notescont;
        FXMLLoader notesloader;

        MWC = this;
    	thisscene.setCursor(Cursor.WAIT);

        notesstage = new Stage();
        notesscene = null;
        notesroot = null;
        notescont = null;
        notesstage = new Stage();
        notesloader = new FXMLLoader();
        try
        {
            URL loc = this.getClass().getResource("/FXML/NotesEditor.fxml");
            notesloader.setLocation(loc);
            notesroot = notesloader.load();

            notesstage.setResizable(true);
            notesstage.setAlwaysOnTop(true);
        }
        catch ( IOException e )
        {
            String msg = "actionShowNotes: Exception for stage="+e.getMessage() 
                       + " cause=" + e.getCause();
//            lgr.error(msg,THISMODULE);
            System.out.println(msg);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            notesscene = new Scene(notesroot);
            notesscene.setCursor(Cursor.WAIT);
            notesstage.setScene(notesscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            notescont = notesloader.getController(); // get the controller for this window
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage() + "\nCause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
        
        try 
        {
            notescont.setCont(notesroot,this);
            notescont.setStage(notesstage);
            notescont.setScene(notesscene);
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage() + "\nCause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
        notesstage.setTitle("BibleView Notes Editor"+version);
       	notesscene.setCursor(Cursor.DEFAULT);
    	notesscene.setCursor(Cursor.DEFAULT);

        notesstage.show();
        menuViewNotes.setSelected(true);
    }

    @FXML private void actionSettingsConfig(ActionEvent event) 
    {
        Stage confstage;
        Scene confscene;
        Parent confroot;
        BiblesConfigController confcont;
        FXMLLoader confloader;

        MWC = this;
    	thisscene.setCursor(Cursor.WAIT);

        confstage = new Stage();
        confscene = null;
        confroot = null;
        confcont = null;
        confstage = new Stage();
        confloader = new FXMLLoader();
        try
        {
            URL loc = getClass().getResource("/FXML/BiblesConfig.fxml");
            confloader.setLocation(loc);
            confroot = confloader.load();

            confstage.setResizable(true);
            confstage.setAlwaysOnTop(true);
        }
        catch ( IOException e )
        {
            String msg = "Exception for stage="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            confscene = new Scene(confroot);
            confscene.setCursor(Cursor.WAIT);
            confstage.setScene(confscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            confcont = confloader.getController(); // get the controller for this window
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage() + "\nCause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
        
        try 
        {
            confcont.setCont(confroot);
            confcont.setStage(confstage);
            confcont.setScene(confscene);
            confcont.setScene(confscene);
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage() + "\nCause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
        confstage.setTitle("BibleView Settings Configuration"+version);
       	thisscene.setCursor(Cursor.DEFAULT);
    	confscene.setCursor(Cursor.DEFAULT);

        confstage.show();
    }

    @FXML
    private void actionLibrariesSetup(ActionEvent event) 
    {
        Stage libsustage;
        Scene libsuscene;
        Parent libsuroot;
        LibrariesSetupController libsucont;
        FXMLLoader libsuloader;

        MWC = this;
    	thisscene.setCursor(Cursor.WAIT);

        libsustage = new Stage();
        libsuscene = null;
        libsuroot = null;
        libsucont = null;
        libsustage = new Stage();
        libsuloader = new FXMLLoader();
        try
        {
            URL loc = getClass().getResource("/FXML/LibrariesSetup.fxml");
            libsuloader.setLocation(loc);
            libsuroot = libsuloader.load();
        }
        catch ( IOException e )
        {
            String msg = "Exception for stage - "+ e.getLocalizedMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            libsuscene = new Scene(libsuroot);
            libsuscene.setCursor(Cursor.WAIT);
            libsustage.setScene(libsuscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            libsucont = libsuloader.getController(); // get the controller for this window
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        
        try 
        {
            libsucont.setCont((Parent)libsuroot);
            libsucont.setStage(libsustage);
            libsucont.setScene(libsuscene);
            libsucont.setScene(libsuscene);
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        libsustage.setTitle("BibleView Libraries Settings Configuration"+version);
        libsustage.setResizable(true);
       	thisscene.setCursor(Cursor.DEFAULT);
    	libsuscene.setCursor(Cursor.DEFAULT);

        libsustage.show();
    }

    @FXML@SuppressWarnings("UnusedAssignment")
 private void actionGetBiblesVersions(ActionEvent event)
    {
        FXMLLoader wmloader;
        Parent  wmroot;
        Stage wmstage;
        Scene wmscene;

        wmloader = new FXMLLoader();
        wmroot = null;
        wmstage = null;
        wmscene = null;

        try
        {
            wmstage = new Stage();
            wmloader.setLocation(getClass().getResource("/FXML/DlgWorksMaint.fxml"));
            wmroot = wmloader.load();
            wmscene = new Scene(wmroot);
        }
        catch ( Exception e )
        {
            String msg = e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }

        try
        {
            wmscene = new Scene(wmroot);
            wmstage.setScene(wmscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "NullPointerException for MainWindow actionGetBiblesVersion="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
    }

    @FXML private void actionGetBibleTrans(ActionEvent event) 
    {
        // TODO:
    }

    @FXML 
    private void actionHelpContents(ActionEvent event) 
    {
        @SuppressWarnings("unused")
        File file = null;
        fc = new FileChooser();
        if ( default_download_path == null ) { default_download_path = "/home/ken/Downloads"; }
        fc.setInitialDirectory(new File(default_download_path));
        fc.setTitle("Select Help file");
        fc.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));  // set Extension Filter
        try 
        {
            file = fc.showOpenDialog(thisstage);
        }
        catch ( Exception e ) 
        {
            String msg = "Error on open of file chooser="+e.getMessage();
            lgr.error(msg,THISMODULE);
            System.exit(1);
        }
        
        // TODO:
    }

    @FXML private void HelpAbout(ActionEvent event) 
    {
        AboutUsController aboutcont;
        FXMLLoader aboutloader;
        Stage aboutstage;
        Scene aboutscene;
        Parent aboutroot;

        MWC = this;

        aboutstage = new Stage();
        aboutscene = null;
        aboutcont = null;
        aboutroot  = null;
        aboutstage = new Stage();
        aboutloader = new FXMLLoader();
        try
        {
            URL loc = getClass().getResource("/FXML/AboutUs.fxml");
            aboutloader.setLocation(loc);
            aboutroot = aboutloader.load();

            aboutstage.setResizable(true);
            aboutstage.setAlwaysOnTop(true);
        }
        catch ( IOException e )
        {
            String msg = "Exception for stage=" + e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            aboutscene = new Scene(aboutroot);
            aboutscene.setCursor(Cursor.WAIT);
            aboutstage.setScene(aboutscene);
        }
        catch ( NullPointerException e )
        {
            String msg = "Exception for stage="+e.getMessage();
            lgr.error(msg,THISMODULE);
       	    thisscene.setCursor(Cursor.DEFAULT);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            aboutcont = aboutloader.getController(); // get the controller for this window
        }
        catch (NullPointerException  e)
        {
            String msg = "Exception for stage="+e.getMessage();
            lgr.error(msg,THISMODULE);
     	    thisscene.setCursor(Cursor.DEFAULT);
        }
        
        try 
        {
            aboutcont.setCont((Parent)aboutroot);
            aboutcont.setStage(aboutstage);
            aboutcont.setScene(aboutscene);
        }
        catch (NullPointerException  e)
        {
            String msg = "Error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        aboutstage.setTitle("BibleView Settings Configuration"+version);
  	    thisscene.setCursor(Cursor.DEFAULT);
        aboutscene.setCursor(Cursor.DEFAULT);
        aboutstage.show();
    }

    @FXML private void Exit(ActionEvent event) 
    {
        scrapeScreen();
        saveSettings();
        thisstage.close();
    }

    @FXML
    private void actionShowVerseNumbers(ActionEvent event) {
        // TODO: set show verse numbers in text
    }

    @FXML
    private void actionShowChapterAndVerse(ActionEvent event) {
        // TODO: set show chapter and verse in text
    }
    // =============================  //
    // END ACTION HANDLERS            //
    // =============================  //
    
    // ================================  //
    // PRIVATE METHODS AND FUNCTIONS     //
    // ================================  //
    public Book getWork(String workInitials) 
    {
       return Books.installed().getBook(workInitials);
    }
    
    /**
      * Get just the canonical text of one or more library work entries without any
      * markup.
      *
      * @param workInitials
      *            the work to use
      * @param reference
      *            a reference, appropriate for the work, of one or more entries
      * @return the plain text for the reference
      * @throws BookException 
      * @throws NoSuchKeyException 
      */
    public String getPlainText(String workInitials, String reference) throws BookException, NoSuchKeyException 
    {
        work = getWork(workInitials);

        if ( work == null ) { return ""; }

        BookData data = new BookData(work, key);
        return OSISUtil.getCanonicalText(data.getOsisFragment());
    }

    /**
      * Obtain a SAX event provider for the OSIS document representation of one
      * or more work entries from the library.
      * 
      * @param workInitials
      *            the work to use
      * @param reference
      *            a reference, appropriate for the work form the library, of 
      *            one or more entries
      * @param maxKeyCount 
      * @return a SAX Event Provider to retrieve the reference
      * @throws WorkException 
      * @throws NoSuchKeyException 
      */
     public SAXEventProvider getOSIS(String workInitials, String reference, int maxKeyCount) throws BookException, NoSuchKeyException 
     {
         if (workInitials == null || reference == null) { return null; }

         work = getWork(workInitials);

         if ( BookCategory.BIBLE.equals(work.getBookCategory()) ) 
         {
             key = work.getKey(reference);
             ((Passage) key).trimVerses(maxKeyCount);
         } 
         else 
         {
             key = work.createEmptyKeyList();
             int count = 0;
             for (Key aKey : work.getKey(reference)) 
             {
                 if (++count >= maxKeyCount) { break; }
                 key.addAll(aKey);
             }
         }

         BookData data = new BookData(work, key);

         return data.getSAXEventProvider();
    }

    /**
      * @details
      * While Bible and Commentary are very similar, a Dictionary is read in a
      * slightly different way. It is also worth looking at the JavaDoc for Book
      * that has a way of treating Bible, Commentary and Dictionary the same.
      * 
      * @throws WorkException 
      * @see Work
      */
     public void readDictionary() throws BookException 
     {
         // This just gets a list of all the known dictionaries and picks the
         // first. In a real world app you will probably have a better way
         // of doing this.
         List<Book> dicts = Books.installed().getBooks(BookFilters.getDictionaries());
         Book dict = dicts.get(0);
 
         // If I want every key in the Dictionary then I do this (or something
         // like it - in the real world you want to call hasNext() on an iterator
         // before next() but the point is the same:
         Key keys = dict.getGlobalKeyList();
         Key first = keys.iterator().next();
         //System.out.println("The first Key in the default dictionary is " + first);
         //WorkData data = new WorkData(dict, first);
         //System.out.println("And the text against that key is " + OSISUtil.getPlainText(data.getOsisFragment()));
     }

    /**
      * @brief An example of how to search for various bits of data.
      * 
      * @throws BookException 
      */
    public void search() throws BookException 
    {
        Book bible = Books.installed().getBook(BIBLE_NAME);
 
        // This does a standard operator search. See the search documentation
        // for more examples of how to search
        Key key = bible.find("+moses +aaron");
        //System.out.println("The following verses contain both moses and aaron: " + key.getName());
 
        // You can also trim the result to a more manageable quantity.
        // The test here is not necessary since we are working with a bible. It
        // is necessary if we don't know what it
        // is.
        /*
        if (key instanceof Passage) 
        {
            Passage remaining = ((Passage) key).trimVerses(5);
            System.out.println("The first 5 verses containing both moses and aaron: " + key.getName());
            if (remaining != null) 
            {
                System.out.println("The rest of the verses are: " + remaining.getName());
            } 
            else 
            {
                System.out.println("There are only 5 verses containing both moses and aaron");
            }
        }
        */
    }

    /**
      * An example of how to perform a ranked search.
      * 
      * @throws WorkException
      */
    void rankedSearch() throws BookException 
    {
        boolean rank;
        int total;
        @SuppressWarnings("unused")
        int partial;
        int max;
        int rankCount;
        
        Key results;

        DefaultSearchModifier modifier;

        rank = true;
        max = 20;

        Book bible;
        PassageTally tally;

        // For a more complex example:
        // Rank the verses and show the first 20

        bible = Books.installed().getBook(BIBLE_NAME);
        modifier = new DefaultSearchModifier();
        modifier.setRanked(rank);
        modifier.setMaxResults(max);

        results = bible.find(new DefaultSearchRequest("for god so loved the world", modifier));
        total = results.getCardinality();
        partial = total;
        // we get PassageTallys for rank searches
        if (results instanceof PassageTally || rank) 
        {
            tally = (PassageTally) results;
            tally.setOrdering(PassageTally.Order.TALLY);
            rankCount = max;
            if ( (rankCount > 0) && (rankCount < total) ) 
            {
                // Here we are trimming by ranges, where a range is a set of
                // continuous verses.
                tally.trimRanges(rankCount, RestrictionType.NONE);
                partial = rankCount;
            }
        }
    }    

    /**
      * @details
      * An example of how to do a search and then get text for each range of
      * verses.
      * 
      * @throws WorkException
      * @throws SAXException
      */
    void searchAndShow() throws BookException, SAXException 
    {
         Book bible = Books.installed().getBook(BIBLE_NAME);

        // Search for words like Melchezedik
        key = bible.find("melchesidec~");

        // Here is an example of how to iterate over the ranges and get the text
        // for each.
        // The key's iterator would have iterated over verses.

        // The following shows how to use a stylesheet of your own choosing
        String path = "xsl/cswing/simple.xsl";
        URL xslurl = ResourceUtil.getResource(path);
        // Make ranges  break  on  chapter
        Iterator<VerseRange> rangeIter = ((Passage) key).rangeIterator(RestrictionType.CHAPTER);
        // boundaries.
        while ( rangeIter.hasNext() ) 
        {
            Key range = rangeIter.next();
            BookData data = new BookData(bible, range);
            SAXEventProvider osissep = data.getSAXEventProvider();
            SAXEventProvider htmlsep = new TransformingSAXEventProvider(NetUtil.toURI(xslurl), osissep);
            String text = XMLUtil.writeToString(htmlsep);
            System.out.println("The html text of " + range.getName() + " is " + text);
        }
    }
    
    /**
      * This is an example of the different ways to select a Work from the
      * selection available.
      * 
      * @see org.crosswire.common.config.Config
      * @see Works
      */
    public void pickBible() 
    {
        // The Default Bible - JSword does everything it can to make this work
        work = Books.installed().getBook(BIBLE_NAME);
 
        // And you can find out more too:
        //System.out.println(book.getLanguage());

        // If you want a greater selection of Works:
        List<Book> works = Books.installed().getBooks();
        work = works.get(0);
 
        // Or you can narrow the range a bit
        works = Books.installed().getBooks(BookFilters.getOnlyBibles());
        work = works.get(0);
        // There are implementations of WorkFilter for all sorts of things in
        // the WorkFilters class
        // If you are wanting to get really fancy you can implement your own
        // WorkFilter easily
        List<Book> test = Books.installed().getBooks(new MyWorksFilter("ESV"));
        work = test.get(0);
 
        if ( work != null ) 
        {
            if ( debug_log ) { lgr.info(work.getInitials(),THISMODULE); }
        }
 
        // If you want to know about new books as they arrive:
        //Works.installed().addWorksListener(new MyWorksListener());
    }
    
    @SuppressWarnings("UnusedAssignment")
    public void installWork() 
    {
        String name;
        Installer installer;
        InstallManager imanager;
        Map<String, Installer> installers;

        name = null;
        installer = null; // An installer knows how to install books
        imanager = new InstallManager();
        installers = imanager.getInstallers(); // Ask the Install Manager for a map of all known module sites
        // Get all the installers one after the other
        for ( Map.Entry<String, Installer> mapEntry : installers.entrySet() ) 
        {
            try
            {
                name = mapEntry.getKey();
                installer = mapEntry.getValue();
                if ( debug_log ) {lgr.info(name + ": " + installer.getInstallerDefinition()); }
            }
            catch ( Exception e )
            {
                String msg = "Error reading installers list - \n===>err=" +  e.getMessage();
                lgr.error(msg,THISMODULE);
            }
        } // for()

        name = "CrossWire";

        try 
        {
            // If we know the name of the installer we can get it directly
            installer = imanager.getInstaller(name);
            installer.reloadBookList();
        } 
        catch ( InstallException e ) 
        {
            String msg = "Error reloading book list from installer " + name 
                       + "\n    err=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        // Now we can get the list of books
        // Get a list of all the available books
        List<Book> availableWorks = installer.getBooks();
        work = availableWorks.get(0);

        if ( work == null ) 
        {
            String msg = "Work " + work.getInitials() + " is NOT available";
            if ( debug_log ) { lgr.info(msg,THISMODULE); }
        }
        // get some available books. In this case, just one book.
        try
        {
            availableWorks = installer.getBooks(new MyWorksFilter("ESV"));
            work = availableWorks.get(0);
        }
        catch ( Exception e )
        {
            String msg = "Work ESV is NOT available.\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        if ( work != null ) 
        {
            if ( debug_log ) { lgr.info("Work " + work.getInitials() + " is available",THISMODULE); }
            // Delete the library work, if present
            // At the moment, JSword will not re-install. Later it will, if the
            // remote version is greater.
            try 
            {
                if ( Books.installed().getBook("ESV") != null) 
                {
                    // Make the work unavailable.
                    // This is normally done via listeners.
                    Books.installed().removeBook(work);
                    // Actually do the delete
                    // This should be a call on installer.
                    work.getDriver().delete(work);
                }
            } 
            catch ( BookException ex ) 
            {
                String msg = "Error on work delete - " + ex.getMessage();
                lgr.error(msg,THISMODULE);
            }

            try 
            {
                // Now install it. Note this is a background task.
                installer.install(work);
            } 
            catch ( InstallException ex ) 
            {
                String msg = "Error on Work install for " + work 
                           + "\n    error=" + ex.getMessage();
                lgr.error(msg,THISMODULE);
            }
        }
    }
    
    /**
      * A simple WorksListener that actually does nothing.
      */
    static class MyWorksListener implements BooksListener 
    {
        @Override
        public void bookAdded(BooksEvent ev) {   }
        @Override
        public void bookRemoved(BooksEvent ev) {  }
    }
        
    /**
      * A simple BookFilter that looks for a Bible by name.
      */
    static class MyWorksFilter implements BookFilter 
    {
        private String name;
        MyWorksFilter(String workName) { name = workName; }
        @Override
        public boolean test(Book bk) { return bk.getInitials().equals(name); }
    }
 
    
    /**
      * Obtain styled text (in this case HTML) for a library work reference.
      * 
      * @param workInitials  the Work to use
      * @param reference     a reference, appropriate for the work, of one or more entries
      * @param maxKeyCount 
      * 
      * @return the styled text
      * 
      * @throws NoSuchKeyException 
      * @throws BookException 
      * @throws TransformerException 
      * @throws SAXException 
      * 
      * @see Book
      * @see SAXEventProvider
      */
     public String readStyledText(String workInitials, String reference, int maxKeyCount) 
           throws NoSuchKeyException, BookException, TransformerException, SAXException
     {
         work = getWork(workInitials);
         SAXEventProvider osissep = getOSIS(workInitials, reference, maxKeyCount);
         if (osissep == null) { return ""; }
 
         Converter styler = ConverterFactory.getConverter();
 
         TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) styler.convert(osissep);
 
         // You can also pass parameters to the XSLT. What you pass depends upon
         // what the XSLT can use.
         BookMetaData bmd = work.getBookMetaData();
         boolean direction = bmd.isLeftToRight();
         htmlsep.setParameter("direction", direction ? "ltr" : "rtl");
 
         // Finally you can get the styled text.
         return XMLUtil.writeToString(htmlsep);
    }
     
    public final void generateConfig() 
    {
        // instead of the XML parsing we use the .ini settings and the BiblesConfig FXML
        // controller to maintain these settings.
/*        
        fillChoiceFactory();
        this.config = new Config(BVMsg.gettext("Desktop Options", new Object[0]));
        
        try {
          Document xmlconfig = XMLUtil.getDocument("config");
          Locale defaultLocale = Locale.getDefault();
          ResourceBundle configResources = ResourceBundle.getBundle("config", defaultLocale, (ClassLoader)CWClassLoader.instance(Desktop.class));
          this.config.add(xmlconfig, configResources);
          try {
            this.config.setProperties(ResourceUtil.getProperties("desktop"));
          } catch (IOException ex) {
            ex.printStackTrace(System.err);
            ExceptionPane.showExceptionDialog(null, ex);
          } 
          this.config.localToApplication();
          this.config.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                  if ("BibleDisplay.ConfigurableFont".equals(evt.getPropertyName())) {
                    BibleViewPane view = (BibleViewPane)Desktop.this.getViews().getSelected();
                    SplitBookDataDisplay da = view.getPassagePane();
                    da.getBookDataDisplay().refresh();
                    Desktop.this.reference.refresh();
                  } 
                  if ("BibleDisplay.MaxPickers".equals(evt.getPropertyName())) {
                    BibleViewPane view = (BibleViewPane)Desktop.this.getViews().getSelected();
                    DisplaySelectPane selector = view.getSelectPane();
                    selector.getBiblePicker().enableButtons();
                  } 
                }
              });
        } catch (IOException e) {
          e.printStackTrace(System.err);
          ExceptionPane.showExceptionDialog(null, e);
        } catch (JDOMException e) {
          e.printStackTrace(System.err);
          ExceptionPane.showExceptionDialog(null, (Throwable)e);
        } 
*/
      }

    final void fillChoiceFactory() 
    {
        /*
        refreshBooks();
        Translations.instance().register();
        Map<String, Class<Converter>> converters = ConverterFactory.getKnownConverters();
        Set<String> keys = converters.keySet();
        String[] names = keys.<String>toArray(new String[keys.size()]);
        ChoiceFactory.getDataMap().put("converters", names);
        ConfigurableSwingConverter cstyle = new ConfigurableSwingConverter();
        String[] cstyles = cstyle.getStyles();
        ChoiceFactory.getDataMap().put("cswing-styles", cstyles);
        */
    }

    protected final void refreshBooks() 
    {
        hasDictionaries = (Defaults.getDictionary() != null);
        hasCommentaries = (Defaults.getCommentary() != null);
        boolean newRefBooks = (hasDictionaries || hasCommentaries);

        if ( newRefBooks != hasRefWorks ) 
        {
            if ( reference != null) 
            {
                if ( !newRefBooks ) 
                {
                    splitMultiBookPane.setDividerPosition(1,8000.0);
                } 
                else 
                {
                    int norm = (int) (splitMultiBookPane.getMaxWidth() * 0.8);
                    splitMultiBookPane.setDividerPosition(1,norm);
                }
                hasRefWorks = newRefBooks;
            }
        }
    }
  
    @SuppressWarnings("unused")
    public void checkForBooks() 
    {
        if ( !bibles.isEmpty() ) { return; }

        String msg;
        String titleMsg;
        String biblestitle;
        String contextmsg;
        // select a new database and update the ini file
        File file;
        FileDialogController biblescont;

        FXMLLoader biblesloader;
        Stage biblesstage;
        Scene biblesscene;
        Parent biblesroot;

        Alert alert;
        
        List<Book> bibleslist;
        Optional<ButtonType> option;

        MWC = this;
        fc  = new FileChooser();

        bibleslist = Books.installed().getBooks(BookFilters.getBibles());

        biblesstage = new Stage();
        biblesscene = null;
        biblesroot = null;

        biblestitle = BVMsg.gettext("Install Bibles?", new Object[0]);
        contextmsg = "You have no Bibles installed. "
                   + "Do you wish to install some now ? ";
        titleMsg = "Works Library";
        file = fc.showOpenDialog(biblesstage);
        if ( file != null ) 
        {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Works Selections");
            alert.setHeaderText("New Works Selection");
            msg = "You have chosen " + file + " as the selected ";
            msg += "works folder. \n\n";
            msg += "Choosing OK will use this file. CANCEL will not change.";
            alert.setContentText(msg);
            option = alert.showAndWait();

            if ( (option.get() == null) || (option.get() == ButtonType.CANCEL) )
            {
                return;
            }
        }
        biblescont = null;
        biblesstage = new Stage();
        biblesloader = new FXMLLoader();
        try
        {
            URL loc = getClass().getResource("/FXML/FileDialog.fxml");
            biblesloader.setLocation(loc);
            biblesroot = biblesloader.load();

            biblesstage.setResizable(true);
            biblesstage.setAlwaysOnTop(true);
        }
        catch ( IOException e )
        {
            msg = "Exception for stage="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            biblesscene = new Scene(biblesroot);
            biblesscene.setCursor(Cursor.WAIT);
            biblesstage.setScene(biblesscene);
        }
        catch ( NullPointerException e )
        {
            msg = "NullPointerException for MainWindow scene="+e.getMessage();
            lgr.error(msg,THISMODULE);
            Thread.currentThread().interrupt();
            System.exit(1);
        }

        try
        {
            biblescont = biblesloader.getController(); // get the controller for this window
        }
        catch (NullPointerException  e)
        {
            msg = "Error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
        }

        try 
        {
            biblescont.setRoot(biblesroot);
            biblescont.setStage(biblesstage);
            biblescont.setScene(biblesscene);
            biblescont.setScene(biblesscene);
        }
        catch (NullPointerException  e)
        {
            msg = "Error=" + e.getMessage() + "\nCause=" + e.getCause();
            lgr.error(msg,THISMODULE);
        }
        biblesstage.setTitle("BibleView Settings Configuration"+version);
        thisscene.setCursor(Cursor.DEFAULT);
        biblesscene.setCursor(Cursor.DEFAULT);

        biblesstage.show();            
        /*
        int reply = CWOptionPane.showConfirmDialog(this, msg, title, 2, 3);
        if (reply == 0)
        {
            //doBooks();
        }
        */
    }
    
    private void selectFolder() 
    {
        String path = versionsPath;
        choice = new File(path);
        if ( !choice.exists() )
        {
            String msg = "Pathname error - " + choice.getAbsolutePath();
            lgr.error(msg,THISMODULE);
        }
    } 

    /**
     * @brief Create the directory tree list for the library
     * 
     * @details
     * Returns a TreeItem representation of the specified directory
     * 
     * @param dirpath String
     */
    @SuppressWarnings("UnusedAssignment")
    private void getNodesForDirectory(String dirpath) throws Exception
    {
        if ( (dirpath == null) || dirpath.isBlank() ) { return; }

        @SuppressWarnings("unused")
        int filecnt;
        @SuppressWarnings("unused")
        int x;
        int len;
        int st;
        int start;
        int end;

        @SuppressWarnings("unused")
        String dirname;

        Image img1;
        Image img2;

        HBox CustomLeaf;
        Label lblText;
        ImageView imgLeaf;
        
        List<TreeItem<Object>> dirlist;
        List<TreeItem<Object>> files;

        filecnt = 0;
        x = 0;
        start = 0;
        end = 0;

        dirname = "";

        dirlist = null;

        // to build leaf image and text together //
        imgLeaf = null;
        CustomLeaf = null;
        lblText = null;
        //                                       //
        // initialize the vars used //
        bibles = FXCollections.observableArrayList();
        dirs = new DirectoryChooser();
        files = new ArrayList<>();
        
        TreeItem<Object> subDirectory;
        DirectoryStream<Path> directoryStream;

        CustomLeaf = new HBox();
        lblText = new Label("");
        lblText = new Label("");
        imgLeaf = new ImageView();
        // only the root node will have one of these images 
        img1 = new Image(this.getClass().getResourceAsStream("/Images/openbook-20.png"));
        img2 = new Image(this.getClass().getResourceAsStream("/Images/book-16_red.png"));
        imgLeaf.setImage(img1);
        //                            //

        len = dirpath.length();
        st  = dirpath.lastIndexOf(File.separator)+ 1;
        lblText.setText(dirpath.substring(st, len));

        CustomLeaf.getChildren().add(imgLeaf);
        CustomLeaf.getChildren().add(lblText);
        subDirectory = new TreeItem<>(CustomLeaf);
        try 
        {
            treeLibraryView.setRoot(subDirectory);
        } 
        catch ( Exception e ) 
        {
            String msg = "Error creating Root..." + e.getMessage();
            lgr.error(msg,THISMODULE);
            throw e;
        }
        // start a directory stream to get the files listing //
        try 
        {
            directoryStream = Files.newDirectoryStream(Paths.get(dirpath));
            for ( Path path: directoryStream ) 
            {
                start = 0;
                end   = 0;
                CustomLeaf = new HBox();
                lblText = new Label();
                imgLeaf = new ImageView();
                
                if ( Files.isDirectory(path) ) 
                {
                    len = path.toString().length();
                    start = len-2;
                    end = len;
                    lblText.setText(path.toString().substring(start,end));
                    imgLeaf.setImage(img2);
                    CustomLeaf.getChildren().add(imgLeaf);
                    CustomLeaf.getChildren().add(lblText);

                    getSubLeafs(path, subDirectory);
                    if ( !dirlist.isEmpty() ) 
                    {
                        dirlist.add(subDirectory); 
                        treeLibraryView.getRoot().getChildren().addAll(dirlist);
                    }

                    if ( !files.isEmpty() ) { treeLibraryView.getRoot().getChildren().addAll(files); }
                }
                else 
                {
                    files.add(getLeafs(path));
                }
            }
        }
        catch ( IOException e ) 
        {
            String msg = "getNodesForDirectory(): Error building leafs..." + e.getMessage()
                       + "\n    Dirs=" + dirs
                       + "\n    files=" + files;
            lgr.error(msg,THISMODULE);
            throw e;
        }
    } // getNodesForDirectory()

    @SuppressWarnings("UnusedAssignment")
    private TreeItem<Object> getLeafs(Path subPath) 
    {
        @SuppressWarnings("unused")
        int len;
        int st;
        int end;

        String strPath;

        HBox CustomLeaf;

        Label lblText;

        ImageView imgLeaf;
        Image img;

        TreeItem<Object> leafs;

        len = 0;
        st  = 0;
        end = 0;

        CustomLeaf = null;
        
        img = new Image(this.getClass().getResourceAsStream("/Images/book-16_orange.png"));        // to build leaf image and text together //

        lblText = null;
        imgLeaf = null;
        CustomLeaf = null;
        leafs = null;
        strPath = "";
        //                                       //

        try 
        {
            CustomLeaf = new HBox();
            lblText = new Label();
            imgLeaf = new ImageView();

            imgLeaf.setImage(img);

            strPath = subPath.toString();

            len = strPath.length();
            st  = strPath.lastIndexOf(File.separator)+ 1;
            end = strPath.lastIndexOf(".");
            lblText.setText(strPath.substring(st, end));
            CustomLeaf.getChildren().add(imgLeaf);
            CustomLeaf.getChildren().add(lblText);
            leafs = new TreeItem<>(CustomLeaf);
        } 
        catch ( Exception e ) 
        {
            String msg = "Error getting leaf"+e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return leafs;
    }

    @SuppressWarnings("UnusedAssignment")
    private void getSubLeafs(Path subPath, TreeItem<Object> parent) 
    {
        @SuppressWarnings("unused")
        String strPath;
        String subTree;
        String msg;
        
        Label lblText;
        Image img;
        ImageView imgLeaf;

        TreeItem<Object> p;

        msg = null;


        img = new Image(this.getClass().getResourceAsStream("/Images/book-48_orange.png"));
        p = parent;

        if ( p == null ) { return; }

        // to build leaf image and text together //
        HBox CustomLeaf;
        //                                       //
        
        try  
        {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(subPath.toString()));
            for( Path subDir: directoryStream ) 
            {
                imgLeaf = null;
                lblText = new Label();
                
                if ( (subDir == null ) || !Files.isReadable(subPath) ) { break; }

                // explicit search for files because we dont want to get sub-sub-directories
                if ( !Files.isDirectory(subDir) ) 
                {
                    CustomLeaf = new HBox();
                    imgLeaf = new ImageView();
                    
                    imgLeaf.setImage(img);
                    strPath = subPath.toString();
                    subTree = subDir.toString();
                    lblText.setText(subTree);
                    CustomLeaf.getChildren().add(imgLeaf);
                    CustomLeaf.getChildren().add(lblText);
                    TreeItem<Object> subLeafs = new TreeItem<>(CustomLeaf);
                    p.getChildren().add(subLeafs);
                }
            }
        } 
        catch (IOException e) 
        {
            msg = "error creating leaf..."+e.getMessage();
            lgr.error(msg,THISMODULE);
        }
    }

    @SuppressWarnings("unused")
    private ObservableList<String> getFiles(File dirpath) 
    {
        int filecnt;
        int x;

        String dirname;
        String lbl;

        File[] filelist;

        ObservableList<String> files;
        /* iterate through the folder and create one leaf item for each file */
        filecnt = 0;
        x = 1;
        dirname = "";
        lbl = "";
        filelist = null;
        files = FXCollections.observableArrayList();

        filelist = dirpath.listFiles();
        filecnt = filelist.length;
        try 
        {
            if ( filecnt > 0 ) 
            {
                for ( x = 0; x < filecnt; x++ ) 
                {
                    if ( !filelist[x].isDirectory() ) { files.add(filelist[x].getName()); }
                }
            }
        } 
        catch ( Exception e )
        {
            String msg = "getFiles(): error - "+ e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        return files;
    }
        
    private String getOSISID(String fil) 
    {
        String osisID = null;
        DocumentBuilder builder = null;
        Document doc = null;

        // open the xml file
        try 
        {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } 
        catch ( ParserConfigurationException e ) 
        {
            String msg = "getOSISID(): bulder exception - "+ e.getMessage();
            lgr.error(msg,THISMODULE);
        }
        

        try 
        {
            fil = System.getProperty("user.home")+ ".sword/" + fil;
            doc = builder.parse(new File(fil));
            doc.getDocumentElement().normalize();
        } 
        catch ( SAXException | IOException se ) 
        {
            String msg = "getOSISID(): SAX error in " + 
                         fil + "\nerror: "+ se.getMessage();
            lgr.error(msg,THISMODULE);
        }
        
        // search for the osisid element and save the value
        try 
        {
            NodeList nodeList = doc.getElementsByTagName("osisText");
            for (int itr = 0; itr < nodeList.getLength(); itr++)   
            {  
                Node node = nodeList.item(itr);  
                if (node.getNodeType() == Node.ELEMENT_NODE)   
                {  
                    Element eElement = (Element)node;  
                    osisID =  eElement.getAttribute("osisIDWork");
                }
            }
        } 
        catch ( Exception e ) 
        {
            String msg = "getOSISID(): SAX error in " + fil 
                       + "\n    error: "+ e.getMessage();
            lgr.error(msg,THISMODULE);
            osisID = "unknown";
        }        
        return osisID;
    }

    private void displayDefaultText()
    {
        int chapter;
        int st;

        String startchap;

        Verse start;
        VerseRange range;
        BibleBook book;
        Versification v11n;

        chapter = -1;
        st = -1;

        book = null;
        start = null;
        range = null;
        
        v11n = null;

        startchap = "";


        try // try to find default text
        {
            v11n = new Versification("kjv",null,null,null,null);
            start = new Verse(v11n,book,1,1);
            book = start.getBook(); //get book from dropdown and create book
            chapter = start.getChapter(); // get chapter from verse object
            v11n = start.getVersification();
            range = new VerseRange(v11n, start, new Verse(v11n, book, chapter, v11n.getLastVerse(book, chapter)));
        }
        catch ( Exception e )
        {
            String msg = "Error finding chapter/book - " + e.getMessage()
                       + "\n    start=" + start.getName() 
                       + "\t v11n=" + v11n.getName();
            lgr.error(msg,THISMODULE);
            throw e;
        }

        try
        {
            int intst = 0;
            
            intst = Integer.parseInt(startchap);
            st = start.indexOf(key.get(intst));
            startchap = cboChapter.getSelectionModel().getSelectedItem();
        }
        catch ( Exception e )
        {
            String msg = "Error finding chapter - key=" + startchap 
                       + "\n    error=" + e.getMessage();
            lgr.error(msg,THISMODULE);
            throw e;
        }

        
        textSrch.setText("");
        textKey.setText(range.getName());
        doGoPassage();
    }

    private void doStyling(String theme) 
    {
        if ( theme.isBlank() ) { theme = "Normal"; }
        
        switch (theme) {
        case "Normal" -> {
            menuSettingsSetNormalTheme.setSelected(true);
            menuSettingsSetMintTheme.setSelected(false);
            menuSettingsSetDarkTheme.setSelected(false);
            menuSettingsSetColorfulTheme.setSelected(false);
            menuSettingsSetFancyTheme.setSelected(false);
            selectedTheme = "Normal";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/normalstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setNormalStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
         }
        case "Mint" -> {
            menuSettingsSetNormalTheme.setSelected(false);
            menuSettingsSetMintTheme.setSelected(true);
            menuSettingsSetDarkTheme.setSelected(false);
            menuSettingsSetColorfulTheme.setSelected(false);
            menuSettingsSetFancyTheme.setSelected(false);
            selectedTheme = "Mint";
            thisscene.getStylesheets().clear();
            try 
            {
                String css = this.getClass().getResource("/CSS/mintstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setMintStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
         }
        case "Dark" -> {
            menuSettingsSetNormalTheme.setSelected(false);
            menuSettingsSetMintTheme.setSelected(false);
            menuSettingsSetDarkTheme.setSelected(true);
            menuSettingsSetColorfulTheme.setSelected(false);
            menuSettingsSetFancyTheme.setSelected(false);
            selectedTheme = "Dark";
            thisscene.getStylesheets().clear();
            try 
            {
                String css = this.getClass().getResource("/CSS/darkstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setDarkStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
          }
        case "Colorful" -> {
            menuSettingsSetNormalTheme.setSelected(false);
            menuSettingsSetMintTheme.setSelected(false);
            menuSettingsSetDarkTheme.setSelected(false);
            menuSettingsSetColorfulTheme.setSelected(true);
            menuSettingsSetFancyTheme.setSelected(false);
            selectedTheme = "Colorful";
            thisscene.getStylesheets().clear();
            try 
            {
                String css = this.getClass().getResource("/CSS/colorfulstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setColorfulStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
          }
        case "Fancy" -> {
            menuSettingsSetNormalTheme.setSelected(false);
            menuSettingsSetMintTheme.setSelected(false);
            menuSettingsSetDarkTheme.setSelected(false);
            menuSettingsSetColorfulTheme.setSelected(false);
            menuSettingsSetFancyTheme.setSelected(true);
            selectedTheme = "Fancy";
            thisscene.getStylesheets().clear();
            try 
            {
                String css = this.getClass().getResource("/CSS/fancystyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setFancyStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
          }
        default -> {
            menuSettingsSetNormalTheme.setSelected(true);
            menuSettingsSetMintTheme.setSelected(false);
            menuSettingsSetDarkTheme.setSelected(false);
            menuSettingsSetColorfulTheme.setSelected(false);
            selectedTheme = "Normal";
            thisscene.getStylesheets().clear();
            try {
                String css = this.getClass().getResource("/CSS/normalstyles.css").toExternalForm();
                thisscene.getStylesheets().add(css);
            }
            catch ( NullPointerException e )
            {
                String msg = "setNormalStyle(): Exception for MainWindow scene="+e.getMessage();
                lgr.error(msg,THISMODULE);
            }
         }
        } // switch()
    } // doStyling()

    private void doGoPassage()
    {
        setKey(textKey.getText());
        if ( !key.isEmpty() ) 
        {
            textSrch.setText("");
            switch ( mode )
            {
            case Mode.CLEAR -> thisstage.setTitle("BibleView ");
            case Mode.PASSAGE -> thisstage.setTitle("BibleView - PASSAGE");
            case Mode.SEARCH -> thisstage.setTitle("BibleView - SEARCH");
            default -> thisstage.setTitle("");
            }
        }
    }

    private void setKey(String newKey) 
    {
        if ( (selected == null) || (selected.length == 0) ) { return; }

        try 
        {
            
            key = selected[0].getKey(newKey); 
        } 
        catch (Exception e) 
        {
            String msg = "No such key exception or null - " + e.getMessage();
            lgr.error(msg,THISMODULE);
        } 
    }

    private void showExceptionDialog(String msg,Exception e)
    {
        // TODO:
    }

    private void fillBibleChoiceBox() 
    {
        ObservableList<String> jvmChoices = FXCollections.observableArrayList();
        jvmChoices.add("KJV");
        jvmChoices.add("NKJVA");
        jvmChoices.add("NAS");
        jvmChoices.add("ASV");
        jvmChoices.add("ISV");
        this.cboVersion.setItems(jvmChoices);
        this.cboVersion.getSelectionModel().select(0);
    }
    
    private void fillBooksChoiceBox() 
    {
        ObservableList<String> booksChoices = FXCollections.observableArrayList(
            "Genesis",
            "Exodus",
            "Levidicus",
            "Numbers",
            "Duteronomy",
            "Joshua",
            "Judges",
            "Ruth",
            "I Samuel",
            "II Samuel",
            "I Kings",
            "II Kings",
            "I Chronicles",
            "II Chronicles",
            "Ezra",
            "Nehemiah",
            "Ester",
            "Job",
            "Psalms",
            "Proverbs",
            "Song of Solomon",
            "Isiah",
            "Jeremiah",
            "Lamentations",
            "Ezekiel",
            "Daniel",
            "Hosea",
            "Joel",
            "Amos",
            "Obadiah",
            "Jonah",
            "Micah",
            "Nahum",
            "Habakkuk",
            "Zephaniah",
            "Haggai",
            "Zechariah",
            "Malachi",
            "Mathew",
            "Mark",
            "Luke",
            "John",
            "Acts",
            "Romans",
            "I Corinthians",
            "II Corinthians",
            "Galatians",
            "Ephesians",
            "Philipians",
            "Colossians",
            "I Thessalonians",
            "II Thessalonians",
            "I Timothy",
            "II Timothy",
            "Titus",
            "Philemon",
            "Hebrews",
            "James",
            "I Peter",
            "II Peter",
            "I John",
            "II John",
            "III John",
            "Jude",
            "Revelation");
        cboBooks.setItems(booksChoices);
        cboBooks.getSelectionModel().select(0);
    }

    private String getPair(String key, String value) { return NamePair.getOrDefault(key, value); }

    /**
     * @brief Book name and OSIS pair
     * 
     * @details 
     * Copyright CrossWire Bible Society, 2005 - 2016
     * 
     * These book names do not need to be internationalized.<br>
     * They are the internal standard OSIS names for the books of the Bible.
     */
    private void createNamePair() 
    {
        NamePair.put("Genesis","Gen");
        NamePair.put("Exodus","Exod");
        NamePair.put("Levidicus","Lev");
        NamePair.put("Numbers","Num");
        NamePair.put("Deuteronomy","Deut");
        NamePair.put("Joshua","Josh");
        NamePair.put("Judges","Judg");
        NamePair.put("Ruth","Ruth");
        NamePair.put("I Samuel","1Sam");
        NamePair.put("II Samuel","2Sam");
        NamePair.put("I Kings","1Kgs");
        NamePair.put("II Kings","2Kgs");
        NamePair.put("I Chronicles","1Chr");
        NamePair.put("II Chronicles","2Chr");
        NamePair.put("Ezra","Ezra");
        NamePair.put("Nehemiah","Neh");
        NamePair.put("Esther","Esth");
        NamePair.put("Job","Job");
        NamePair.put("Psalms","Ps");
        NamePair.put("Proverbs","Prov");
        NamePair.put("Eccleseastese","Eccl");
        NamePair.put("Song of Solomon","Song");
        NamePair.put("Isiah","Isa");
        NamePair.put("Jeremiah","Jer");
        NamePair.put("Lamentations","Lam");
        NamePair.put("Ezekiel","Ezek");
        NamePair.put("Daniel","Dan");
        NamePair.put("Hosea","Hos");
        NamePair.put("Joel","Joel");
        NamePair.put("Amos","Amos");
        NamePair.put("Obadiah","Obad");
        NamePair.put("Jonah","Jonah");
        NamePair.put("Micah","Mic");
        NamePair.put("Nahum","Nah");
        NamePair.put("Habakkuk","Hab");
        NamePair.put("Zephaniah","Zeph");
        NamePair.put("Haggai","Hag");
        NamePair.put("Zechariah","Zech");
        NamePair.put("Malachi","Mal");
        NamePair.put("Mathew","Matt");
        NamePair.put("Mark","Mark");
        NamePair.put("Luke","Luke");
        NamePair.put("John","John");
        NamePair.put("Acts","Acts");
        NamePair.put("Romans","Rom");
        NamePair.put("I Corinthians","1Cor");
        NamePair.put("II Corinthians","2Cor");
        NamePair.put("Galatians","Gal");
        NamePair.put("Ephesians","Eph");
        NamePair.put("Philipians","Phil");
        NamePair.put("Colossians","Col");
        NamePair.put("I Thessalonians","1Thess");
        NamePair.put("II Thessalonians","2Thess");
        NamePair.put("I Timothy","1Tim");
        NamePair.put("II Timothy","2Tim");
        NamePair.put("Titus","Titus");
        NamePair.put("Philemon","Phlm");
        NamePair.put("Hebrews","Heb");
        NamePair.put("James","Jas");
        NamePair.put("I Peter","1Pet");
        NamePair.put("II Peter","2Pet");
        NamePair.put("I John","2John");
        NamePair.put("II John","2John");
        NamePair.put("III John","2John");
        NamePair.put("Jude","Jude");
        NamePair.put("Revelation","Rev");
    }

    private void fillChapterChoiceBox() 
    {
        chapters = FXCollections.observableArrayList();
        chapters.clear();
        chapters.addAll("1","2","3","4","5","6","7","8","9","10",
                      "11","12","13","14","15","16","17","18","19","20",
                      "21","22","23","24","25","26","27","28","29","30",
                      "31","12","33","34","35","36","37","38","39","40",
                      "41","12","43","44","45","46","47","48","49","50",
                      "51","12","53","54","55","56","57","58","59","60",
                      "61","62","63","64");
        cboChapter.setItems(chapters);
        cboChapter.getSelectionModel().select(0);
    }

    private void loadVerseArray() 
    {
        verses = FXCollections.observableArrayList();
        verses.clear();
        verses.addAll("1","2","3","4","5","6","7","8","9","10",
                      "11","12","13","14","15","16","17","18","19","20",
                      "21","22","23","24","25","26","27","28","29","30",
                      "31","12","33","34","35","36","37","38","39","40");
    }

    private void displaySettings() 
    {
        try {
            thisstage.setX(uix);
            thisstage.setY(uiy);
            thisstage.setWidth(uiw);
            thisstage.setHeight(uih);
        }
        catch ( Exception e ) {
            String msg = "displaySettings(): error = " + e.getMessage();
            if ( debug_log ) { lgr.error(msg,THISMODULE); }
        }
        
        doStyling(selectedTheme);
    }

    public void showStatusMsg1(String msg)
    {
        statusMsg1.setText(msg);
    }

    public void showStatusMsg2(String msg)
    {
        statusMsg2.setText(msg);
    }

    private void createStylesheet() 
    {
        // TODO:
    }

    private void loadStylesheet() 
    {
        // TODO:
    }

    private void initLocalObjects()
    {
        // TODO: init any objects for scene here
    }

    private void scrapeScreen() 
    {
        uix = thisstage.xProperty().doubleValue();
        uiy = thisstage.yProperty().doubleValue();
        uih = thisstage.getHeight();
        uiw = thisstage.getWidth();
    }

    private void initLocalVars() 
    {
        debug_log = true;
        hasDictionaries = false;
        hasCommentaries = false;
        hasRefWorks     = false;
        compareShowing  = false;
        DefaultDictionary_Hidden  = false;
        DefaultCommentary_Hidden  = false;
        DefaultHebrewParse_Hidden = false;

        activeTabCount = 0;
        activeChapt    = 1;

        uix = -1.0;
        uiy = -1.0;
        uiw = -1.0;
        uih = -1.0;

        root      = null;

        version = "v0.85";
        selectedTheme = "Normal";

        defaultLang = "en";        
        default_download_path = System.getProperty("user.dir")+".sword/";
        activePath = System.getProperty("user.home")+".sword/";
        iniFilePath = System.getProperty("user.home")+"/bin/BibleView/BibleViewFX.ini";
    }
    // ================================  //
    // END PRIVATE METHODS AND FUNCTIONS //
    // ================================  //
    
    
    // ======================================
    // INI FILE HANDER                     //
    // ======================================
    private void readSettings() 
    {
        String yesno;

        settings = new IniUtil();

        try
        {
            yesno = settings.getSetting(MAINSECTION, "debug_log");
            debug_log = yesno.contains("true");
        }
        catch ( NullPointerException e )
        {
            debug_log = false;
        }


        try
        {
            strAppPath =  settings.getSetting(MAINSECTION,"AppPath");
        }
        catch ( NullPointerException e )
        {
            strAppPath = System.getProperty("user.dir");
        }

        try
        {
            version =settings.getSetting(MAINSECTION, "AppVersion");
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            version = "v0.87";
        }

        try {
            selectedTheme = settings.getSetting(MAINSECTION, "skin");
        }
        catch ( NullPointerException e ) 
        {
            selectedTheme = "Normal";
        }

        try
        {
            default_download_path = settings.getSetting(MAINSECTION, "defaultDownloadPath");
        }
        catch ( NullPointerException e )
        {
            default_download_path = System.getProperty("user.home") + ".sword";
        }

        try
        {
            versionsPath = settings.getSetting(LIBSECTION, "versions");
        }
        catch ( NullPointerException e )
        {
            default_download_path = System.getProperty("user.home") + ".sword";
        }

        try
        {
            defaultLang = settings.getSetting(MAINSECTION, "defaultLang");
        }
        catch ( NullPointerException e )
        {
            defaultLang = "en";
        }

        try
        {
            double x = Double.parseDouble(settings.getSetting(MAINSECTION, "UIX"));
            uix = x;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uix = 300.0;
        }

        try
        {
            double y = Double.parseDouble(settings.getSetting(MAINSECTION, "UIY"));
            uiy = y;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiy = 300.0;
        }

        try
        {
            double w = Double.parseDouble(settings.getSetting(MAINSECTION, "UIW"));
            uiw = w;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uiw = 1700.0;
        }

        try
        {
            double h = Double.parseDouble(settings.getSetting(MAINSECTION, "UIH"));
            uih = h;
        }
        catch ( NullPointerException | NumberFormatException e )
        {
            uih = 1000.0;
        }



        settings = null;
    }
    
    private void saveSettings() 
    {
        String yesno = "false";

        settings = new IniUtil();

        if (debug_log) {yesno = "true"; }
        settings.setValue(MAINSECTION, "debug_log",yesno);

        try {
            settings.setValue(MAINSECTION, "skin", selectedTheme);
        }
        catch ( Exception e ) {
            settings.setValue(MAINSECTION, "skin", "Normal");
        }
            
        try
        {
            settings.setValue(MAINSECTION, "UIX",Double.toString(uix));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIX","300.0");
        }

        try
        {
            settings.setValue(MAINSECTION, "UIY",Double.toString(uiy));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIY","300.0");
        }
       
        try
        {
            settings.setValue(MAINSECTION, "UIW",Double.toString(uiw));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIW","1204.0");
        }

        try
        {
            settings.setValue(MAINSECTION, "UIH",Double.toString(uih));
        }
        catch (NullPointerException | NumberFormatException e )
        {
            settings.setValue(MAINSECTION, "UIH","800.0");
        }

        try
        {
            settings.setValue(MAINSECTION, "activeText",activeText);
        }
        catch ( NullPointerException e )
        {
            settings.setValue(MAINSECTION, "activeText","kjv");
        }

        try
        {
            settings.setValue(MAINSECTION, "activeBook",activeBook);
        }
        catch ( NullPointerException e )
        {
            settings.setValue(MAINSECTION, "activeBook","Gen");
        }

        settings = null;
    }
    // =================================== //
    // END INI FILE HANDER                 //
    // =================================== //
} // end class

/* hold this as defaults
[MAIN]
AppVersion=v0.81
AppPath="/home/ken/Projects/Java/BibleView"
UIX=451.0
UIY=223.0
UIW=1242.0
UIH=789.0
skin=Normal
debug_log=true

[CONFIG]
UIX=300.0
UIY=100.0
UIW=1004.0
UIH=636.0
debug_log=true;

[LIBRARIES]
debug_log=true
UIX=574.0
UIY=379.0
UIW=971.0
UIH=418.0
defaultLibPath=/home/ken/bin/BibleView
dictionaries=/home/ken/bin/BibleView/LIBRARY/Dictionaries
commentaries=/home/ken/bin/BibleView/LIBRARY/Commentaries
notes=/home/ken/bin/BibleView/LIBRARY/Notes
lexicons=/home/ken/bin/BibleView/LIBRARY/Lexicons
maps=/home/ken/bin/BibleView/LIBRARY/Maps
devotions=/home/ken/bin/BibleView/LIBRARY/Devotions

[KJV]
data_path=LIBRARY/kjv
TextSource=https\://gitlab.com/crosswire-bible-society/kjv
source_type=OSIS
InstallSize=4232647
encoding=UTF-8
SwordVersionDate=2023-07-19
Version=3.1
MinimumVersion=1.5.9
Lang=en
LCSH=Bible.English
OSISVersion=2.1.1
DistributionLicense=GPL
About=This is the King James Version of the Holy Bible (also known as the Authorized Version) with embedded Strong's Numbers.  The rights to the base text are held by the Crown of England.par  The Strong's numbers in the OT were obtained from The Bible Foundation\: http\://www.bf.org.  The NT Strong's data was obtained from The KJV2003 Project at CrossWire\: http\://www.crosswire.org.  These mechanisms provide a useful means for looking up the exact original language word in a lexicon that is keyed to Strong's numbers.parpar Special thanks to the volunteers at Bible Foundation for keying the Hebrew/English data and of Project KJV2003 for working toward the completion of synchronizing the English phrases to the Stephanas Textus Receptus, and to Dr. Maurice Robinson for providing the base Greek text with Strong's and Morphology.par  We are also appreciative of formatting markup that was provided by Michael Paul Johnson at http\://www.ebible.org.  Their time and generosity to contribute such for the free use of the Body of Christ is a great blessing and this derivitive work could not have been possible without these efforts of so many individuals.par  Version 3.1 incorporates a more recent set of TR data from Dr. Maurice Robinson than was used in all the earlier versions. The TR data was obtained in 2016 from the Greek New Testament sources website par https\://sites.google.com/a/wmail.fi/greeknt/home/greeknt par This was integrated into the OSIS source files of an intermediate version 2.9a hosted at par https\://www.crosswire.org/~dmsmith/kjv2011/kjv2.9a/ par  It is in this spirit that we in turn offer the KJV2003 Project text freely for any purpose.par  Any copyright that might be obtained for this effort is held by CrossWire Bible Society \u00a9 2003-2023 and CrossWire Bible Society hereby grants a general public license to use this text for any purpose.par Inquiries and comments may be directed to\:parpar CrossWire Bible Societypar modules@crosswire.orgpar http\://www.crosswire.org
compress_type=ZIP
blocktype=BOOK
versification=KJV
GlobalOptionFilter=OSISFootnotes
GlobalOptionFilter=OSISHeadings
GlobalOptionFilter=OSISRedLetterWords
GlobalOptionFilter=OSISLemma
GlobalOptionFilter=OSISStrongs
GlobalOptionFilter=OSISMorph
Feature=NoParagraphs
Feature=StrongsNumbers
History_1.1=Fixed .conf file GlobalOptionFilters
History_1.2=Added Feature StrongsNumbers to configuration
History_1.3=Added Morph option
History_1.4=Fixed Psalm Titles to use correct GBF tags HT and Ht
History_2.0=Changed New Testament to use a snapshot of the KJV2003 Project
History_2.1=Changed Old Testament to use OSIS tags, removing the last of the GBF markup.  Also updated to 20030624 snapshot of KJV2003.  Compressed.
History_2.2=Updated to 20040121 snapshot of KJV2003.
History_2.3=Fixed bugs.
History_2.4=Fixed bugs.
History_2.5=Fixed bugs.
History_2.6=Fixed bugs. Added Greek from TR.
History_2.6.1=Added GlobalOptionFilter for OSISLemma
History_2.7=Fixed bugs preventing the display of some Strong's Numbers.
History_2.8=(2015-12-20) Moved Ps 119 acrostic titles before verse number. Added Feature for no paragraphs.
History_2.9=(2016-01-21) Added markup to notes. Improved markup of Selah.
History_2.10.2=(2021-04-04) Fixed errant article Strong's markup in Rom.3.26
History_2.11=(2023-06-27) Updated the TR data in 5888 verses. Correction of Ps.2.4\: the Lord \u2192 the <divineName>Lord</divineName>. MOD-448\: many errors with strong as G3688, should be G3588. Five locations where small caps is inappropriate, <divineName>Lord</divineName> change for <divineName>LORD</divineName> Exodus 28\:36; 39\:30, Deuteronomy 28\:58, Jeremiah 23\:6, Zechariah 14\:20. MOD-411\: Psalm 80, Shoshannim\u2013eduth \u2192 Shoshannim\u2013Eduth. MOD-408\: italics in Nahum 1\:3, (<w lemma\=strong\:H01870>hath) \u2192 (<transChange type\=added>hath</transChange>). MOD-358\: Missing red letter markup in Luke 11\:2, red marker moved before 'When'. MOD-413 multiple text issues (italics, spelling, ...). MOD-441\: Missing strong number for the seventh word. MOD-419\: Spurious "or" removed; Exodus 17\:15 "Jehovah\u2013nissi" \u2192 "JEHOVAH\u2013nissi". 1 John 2\:23 has "(but)" whereas Blayney has "[but]"; Correction of some morph errors in I Corinthians 3\:13 A-GMS \u2192 A-GSM; I Corinthians 11\:6 and 14\:24 N-NAM \u2192 N-NSM; Hb 9.13, 10.4\: N-GMP \u2192 N-GPM; I Cor 7\:37 2 change for 12; 1Chr.27.12 Abi-ezer \u2192 abiezer; Exod.17.15\: Jehovah \u2192 JEHOVAH. Correction of Jer.23.6\: L \u2192 LORD; MOD-376\: Mk 1\:10.19 have a wrong strong number\: 1492. The correct number is 3708. Correction of rdg attribute type\="alternative" (2.9) to "alternate".  Misplaced </w> in Numbers 16\:13. Duplicated entry in Eph. 3.20, spurious comma in Gn 1.2 and 2.9. Ezra 5\:3,6; 6.6,13\: morph\="strongMorph\:TH8674 changed for lemma\="strong\:H08674.
History_3.1=(2023-07-19) Conf version updated to 3.1 to match KJVA version.

*/