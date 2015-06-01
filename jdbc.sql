SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE IF NOT EXISTS `Article` (
  `ArticleID` int(10) unsigned NOT NULL,
  `Type` enum('Article','Poster','Speech','None') COLLATE utf8_polish_ci NOT NULL DEFAULT 'None',
  `Name` text COLLATE utf8_polish_ci NOT NULL,
  `LangID` int(10) unsigned NOT NULL DEFAULT '0',
  `ISSN` int(7) unsigned zerofill NOT NULL DEFAULT '0000000',
  `Year` smallint(5) unsigned NOT NULL DEFAULT '0',
  `Issue` text COLLATE utf8_polish_ci NOT NULL,
  `ArticleNo` varchar(50) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `AuthorsFromUnit` text COLLATE utf8_polish_ci NOT NULL,
  `AuthorsNotFromUnitCount` int(10) unsigned NOT NULL DEFAULT '0',
  `AuthorsNotFromUnit` text COLLATE utf8_polish_ci NOT NULL,
  `DOI` varchar(300) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `Url` varchar(500) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `ConferenceName` varchar(300) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `Date` varchar(25) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `Town` varchar(300) COLLATE utf8_polish_ci NOT NULL DEFAULT '',
  `Country` varchar(300) COLLATE utf8_polish_ci NOT NULL DEFAULT ''
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

TRUNCATE TABLE `Article`;
INSERT INTO `Article` (`ArticleID`, `Type`, `Name`, `LangID`, `ISSN`, `Year`, `Issue`, `ArticleNo`, `AuthorsFromUnit`, `AuthorsNotFromUnitCount`, `AuthorsNotFromUnit`, `DOI`, `Url`, `ConferenceName`, `Date`, `Town`, `Country`) VALUES
(1, 'Article', 'Auxiliary-Field Quantum Monte Carlo Simulations of Neutron Matter in Chiral Effective Field Theory ', 2, 0031900, 2014, '113', '182503', 'G. Wlazłowski', 0, 'J. W. Holt, S. Moroz, A. Bulgac, K. J. Roche', '10.1103/PhysRevLett.113.182503', 'http://journals.aps.org/prl/abstract/10.1103/PhysRevLett.113.182503', '', '0000-00-00', '', ''),
(2, 'Article', 'Quantized Superfluid Vortex Rings in the Unitary Fermi Gas', 2, 0031900, 2014, '112', '025301', 'G. Wlazłowski', 0, 'Aurel Bulgac, Michael McNeil Forbes, Michelle M. Kelley, Kenneth J. Roche', '10.1103/PhysRevLett.112.025301', 'http://journals.aps.org/prl/abstract/10.1103/PhysRevLett.112.025301', '', '0000-00-00', '', ''),
(51, 'Article', 'Pion femtoscopy measurements in small systems with ALICE at the LHC', 2, 2100014, 2014, '71', '00051', ' Ł.K. Graczykowski', 0, '', '10.1051/epjconf/20147100051', 'http://www.epj-conferences.org/articles/epjconf/abs/2014/08/epjconf_icnfp2013_00051/epjconf_icnfp2013_00051.html', 'Quark Matter 2014', '19-24.05.2014', 'Darmstadt', 'Niemcy'),
(52, 'Article', 'Properties of N=90 Isotones within the Mean Field Perspective', 2, 0556281, 2014, '89', '014311', 'P. Magierski', 0, 'E. Ganioglu, R. Wyss', '10.1103/PhysRevC.89.014311', 'http://journals.aps.org/prc/abstract/10.1103/PhysRevC.89.014311', '', '', '', '');

CREATE TABLE IF NOT EXISTS `Format` (
  `Name` varchar(20) COLLATE utf8_polish_ci NOT NULL,
  `Format` text COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

TRUNCATE TABLE `Format`;
INSERT INTO `Format` (`Name`, `Format`) VALUES
('Article', '%ISSN%; %ISSN_NAME%; %ARTICLE_NAME%; %AUTHORS_FROM_UNIT%; %AUTHORS_NOT_FROM_UNIT_COUNT%; %AUTHORS_NOT_FROM_UNIT%; %YEAR%; %ISSUE%; %ARTICLE_ID%; %LANG_PL%; %DOI%; %URL% /'),
('Out', 'Zakład Fizyki Jądrowej\nLista publikacji naukowych i wystąpień konferencyjnych w 2014 roku\n\nPublikacje z listy JCR znajdujące się na liście A MNiSW\n%ARTICLES_A%\n\nPublikacje z listy JCR znajdujące się na liście B MNiSW\n%ARTICLES_B%\n\nPublikacje w recenzowanych czasopismach innych niż wymienione w listach A i B\n%ARTICLES_U%\n\nKonferencje i plakaty\n%SPEECH_POSTER%'),
('Poster', '%CONFERENCE_NAME%, %POSTER_NAME%; %AUTHORS_FROM_UNIT%; %AUTHORS_NOT_FROM_UNIT_COUNT%; %AUTHORS_NOT_FROM_UNIT%; %DATE%, %PLACE%, %LANG_PL%, %TYP%'),
('Speech', '%CONFERENCE_NAME%, %SPEECH_NAME%; %AUTHORS_FROM_UNIT%; %AUTHORS_NOT_FROM_UNIT_COUNT%; %AUTHORS_NOT_FROM_UNIT%; %DATE%, %PLACE%, %LANG_PL%, %TYP% ');

CREATE TABLE IF NOT EXISTS `ISSN` (
  `ISSN` int(7) unsigned zerofill NOT NULL,
  `ISSNName` varchar(200) COLLATE utf8_polish_ci NOT NULL,
  `List` enum('LIST_A','LIST_B','UNLISTED') COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

TRUNCATE TABLE `ISSN`;
INSERT INTO `ISSN` (`ISSN`, `ISSNName`, `List`) VALUES
(0031900, 'Physical Review Letters', 'LIST_A'),
(0556281, 'Physical Review C', 'LIST_A'),
(0587425, 'Acta Physica Polonica B', 'LIST_A'),
(1550799, 'Physical Review D', 'LIST_A'),
(1748022, 'Journal of Instrumentation', 'LIST_A'),
(2100014, 'EPJ Web of Conferences', 'UNLISTED');

CREATE TABLE IF NOT EXISTS `Lang` (
  `LangID` int(10) unsigned NOT NULL,
  `NamePL` varchar(100) COLLATE utf8_polish_ci NOT NULL,
  `NameEN` varchar(100) COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

TRUNCATE TABLE `Lang`;
INSERT INTO `Lang` (`LangID`, `NamePL`, `NameEN`) VALUES
(1, 'polski', 'polish'),
(2, 'angielski', 'english');


ALTER TABLE `Article`
  ADD UNIQUE KEY `ArticleID` (`ArticleID`);

ALTER TABLE `Format`
  ADD UNIQUE KEY `Name` (`Name`);

ALTER TABLE `ISSN`
  ADD UNIQUE KEY `ISSN` (`ISSN`);

ALTER TABLE `Lang`
  ADD UNIQUE KEY `LangID` (`LangID`);


ALTER TABLE `Article`
  MODIFY `ArticleID` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=53;
ALTER TABLE `Lang`
  MODIFY `LangID` int(10) unsigned NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=23;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
