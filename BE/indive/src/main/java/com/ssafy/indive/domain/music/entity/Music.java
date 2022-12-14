package com.ssafy.indive.domain.music.entity;

import com.ssafy.indive.domain.member.entity.Member;
import com.ssafy.indive.domain.music.exception.MusicFileNotFoundException;
import com.ssafy.indive.domain.music.service.dto.ServiceMusicModifyRequestDto;
import com.ssafy.indive.global.common.entity.BaseEntity;
import com.ssafy.indive.global.utils.FileUtils;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Generated
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name="music_seq")
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="music_author_seq")
    private Member author;

    @Column(name="music_title")
    private String title;

    @Column(name="music_lyricist")
    private String lyricist;

    @Column(name="music_composer")
    private String composer;

    @Column(name="music_genre")
    private String genre;

    @Column(name="music_description")
    private String description;

    @Lob
    @Column(name="music_lyrics")
    private String lyrics;

    @Column(name="music_release_datetime")
    private LocalDateTime releaseDatetime;

    @Column(name="music_reservation_datetime")
    private LocalDateTime reservationDatetime;

    @Column(name="music_image_origin")
    private String imageOrigin;

    @Column(name="music_image_uuid")
    private String imageUuid;

    @Column(name="music_file_origin")
    private String musicFileOrigin;

    @Column(name="music_file_uuid")
    private String musicFileUuid;

    @Column(name="music_like_count")
    private int likeCount;

    @OneToMany(mappedBy = "music", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MusicLike> musicLikes = new ArrayList<>();

    @OneToMany(mappedBy = "music", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Music(Long seq, Member author, String title, String lyricist, String composer, String genre, String description, String lyrics, LocalDateTime releaseDatetime, LocalDateTime reservationDatetime, String imageOrigin, String imageUuid, String musicFileOrigin, String musicFileUuid, int likeCount) {
        this.seq = seq;
        this.author = author;
        this.title = title;
        this.lyricist = lyricist;
        this.composer = composer;
        this.genre = genre;
        this.description = description;
        this.lyrics = lyrics;
        this.releaseDatetime = releaseDatetime;
        this.reservationDatetime = reservationDatetime;
        this.imageOrigin = imageOrigin;
        this.imageUuid = imageUuid;
        this.musicFileOrigin = musicFileOrigin;
        this.musicFileUuid = musicFileUuid;
        this.likeCount = likeCount;
    }

    // TODO: ???????????? DTO??? ???????????? ????????? ??????????????? ?????????. ????????? ?????????????????????
    public void update(ServiceMusicModifyRequestDto dto) {
        title = dto.getTitle();
        lyricist = dto.getLyricist();
        composer = dto.getComposer();
        genre = dto.getGenre();
        description = dto.getDescription();
        lyrics = dto.getLyrics();
        releaseDatetime = dto.getReleaseDateTime();
        reservationDatetime = dto.getReservationDateTime();
    }

    public void uploadFiles(MultipartFile image, MultipartFile musicFile) {
        // ?????? ??????
        imageOrigin = image.isEmpty() ? null : image.getOriginalFilename();
        imageUuid = image.isEmpty() ? "defaultAlbumCover.png" : FileUtils.saveFile(image);

        // mp3 ????????? null??? ?????? ?????? ??????
        if (musicFile.isEmpty()) throw new MusicFileNotFoundException("?????? ????????? ?????? ???????????? ?????????!");

        // mp3 ??????
        musicFileOrigin = musicFile.getOriginalFilename();
        musicFileUuid = FileUtils.saveFile(musicFile);
    }

    public void plusLikeCount() {
        likeCount++;
    }

    public void minusLikeCount() {
        likeCount--;
    }
}
