package fr.lernejo.fileinjector;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
record MessageObject(int id,
                     String title,
                     String thumbnail,
                     String short_description,
                     String game_url,
                     String genre,
                     String platform,
                     String publisher,
                     String developer,
                     String release_date,
                     String freetogame_profile_url){}
