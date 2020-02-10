-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 10. Feb 2020 um 23:23
-- Server-Version: 10.1.21-MariaDB
-- PHP-Version: 7.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `spiele_projekt`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `freunde`
--

CREATE TABLE `freunde` (
  `spielerid_1` int(11) NOT NULL,
  `spielerid_2` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Daten für Tabelle `freunde`
--

INSERT INTO `freunde` (`spielerid_1`, `spielerid_2`) VALUES
(1, 2),
(1, 3),
(1, 4),
(2, 1),
(2, 3),
(2, 5),
(6, 3),
(7, 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `historie`
--

CREATE TABLE `historie` (
  `game_id` int(11) NOT NULL,
  `user_id1` int(11) NOT NULL,
  `user_id2` int(11) NOT NULL,
  `ausgang` varchar(30) CHARACTER SET utf32 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ligen`
--

CREATE TABLE `ligen` (
  `liga_id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Daten für Tabelle `ligen`
--

INSERT INTO `ligen` (`liga_id`, `name`) VALUES
(0, 'keine Liga'),
(1, 'Bundesliga');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `users`
--

CREATE TABLE `users` (
  `user_id` int(30) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL,
  `online` int(11) NOT NULL,
  `liga_id` int(11) NOT NULL,
  `liga_punkte` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32;

--
-- Daten für Tabelle `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `online`, `liga_id`, `liga_punkte`) VALUES
(1, 'killer123', 'killer123', 0, 0, 3),
(2, 'cooler_dude', 'cooler_dude', 0, 0, 0),
(3, 'fighter04', 'fighter04', 0, 0, 0),
(4, 'qwertz09', 'qwertz09', 0, 0, 0),
(5, 'ronin4', 'ronin4', 0, 0, 0),
(6, 'zerstoerer12', 'zerstoerer12', 0, 0, 0),
(7, 'tommi', 'tommi', 0, 0, 0);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `freunde`
--
ALTER TABLE `freunde`
  ADD PRIMARY KEY (`spielerid_1`,`spielerid_2`),
  ADD KEY `spielerid_2` (`spielerid_2`);

--
-- Indizes für die Tabelle `historie`
--
ALTER TABLE `historie`
  ADD PRIMARY KEY (`game_id`),
  ADD KEY `user_id1` (`user_id1`),
  ADD KEY `user_id2` (`user_id2`);

--
-- Indizes für die Tabelle `ligen`
--
ALTER TABLE `ligen`
  ADD PRIMARY KEY (`liga_id`);

--
-- Indizes für die Tabelle `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `liga_id` (`liga_id`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `historie`
--
ALTER TABLE `historie`
  MODIFY `game_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `ligen`
--
ALTER TABLE `ligen`
  MODIFY `liga_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT für Tabelle `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(30) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `freunde`
--
ALTER TABLE `freunde`
  ADD CONSTRAINT `freunde_ibfk_1` FOREIGN KEY (`spielerid_1`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `freunde_ibfk_2` FOREIGN KEY (`spielerid_2`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `historie`
--
ALTER TABLE `historie`
  ADD CONSTRAINT `historie_ibfk_1` FOREIGN KEY (`user_id1`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `historie_ibfk_2` FOREIGN KEY (`user_id2`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`liga_id`) REFERENCES `ligen` (`liga_id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
