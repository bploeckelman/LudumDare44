package lando.systems.ld44.utils;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld44.Game;

import java.util.HashMap;

public class Audio implements Disposable {

    public static final float MUSIC_VOLUME = 1f;
    public static final boolean shutUpYourFace = false;
    public static final boolean shutUpYourTunes = false;

    public enum Sounds {
    }

    public enum Musics {
    }

    public HashMap<Sounds, SoundContainer> sounds = new HashMap<Sounds, SoundContainer>();
    public HashMap<Musics, Music> musics = new HashMap<Musics, Music>();

    public Music currentMusic;
    public MutableFloat musicVolume;
    public Musics eCurrentMusic;
    public Music oldCurrentMusic;

    private Game game;

    public Audio(Game game) {
        this(!shutUpYourTunes, game);
    }

    public Audio(boolean playMusic, Game game) {
        this.game = game;
//        putSound(Sounds.dog_bork, Gdx.audio.newSound(Gdx.files.internal("audio/dog.mp3")));
//        putSound(Sounds.cat_meow, Gdx.audio.newSound(Gdx.files.internal("audio/cat.mp3")));
//
//        musics.put(Musics.RockHardyWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/RockHardyWithMaster.mp3")));
//        musics.put(Musics.SpaceAmbWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/SpaceAmbWithMaster.mp3")));
//        musics.put(Musics.SpaceDramaWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/SpaceDramaWithMaster.mp3")));
//        musics.put(Musics.SillySpaceDrumsWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/SillySpaceDrumsWithMaster.mp3")));
//        musics.put(Musics.XmenKnockersWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/XmenKnockersWithMaster.mp3")));
//        musics.put(Musics.SpaceFanfareWithMaster, Gdx.audio.newMusic(Gdx.files.internal("audio/SpaceFanfareWithMaster.mp3")));


        musicVolume = new MutableFloat(MUSIC_VOLUME);
        if (playMusic) {
//            currentMusic = musics.get(Musics.SpaceAmbWithMaster);
//            eCurrentMusic = Musics.SpaceAmbWithMaster;
            currentMusic.setLooping(true);
            currentMusic.setVolume(0f);
            currentMusic.play();
            setMusicVolume(MUSIC_VOLUME, 2f);
            // currentMusic.setOnCompletionListener(nextSong);
        }
    }

    public void update(float dt){
        if (currentMusic != null) {
            if (musicVolume.floatValue() == 0f) {
                if (oldCurrentMusic != null) oldCurrentMusic.stop();
                setMusicVolume(MUSIC_VOLUME, 1f);
                currentMusic.play();
            }

            currentMusic.setVolume(musicVolume.floatValue());
        }

        if (oldCurrentMusic != null) {
            oldCurrentMusic.setVolume(musicVolume.floatValue());
        }
    }

    @Override
    public void dispose() {
        Sounds[] allSounds = Sounds.values();
        for (Sounds sound : allSounds) {
            if (sounds.get(sound) != null) {
                sounds.get(sound).dispose();
            }
        }
        Musics[] allMusics = Musics.values();
        for (Musics music : allMusics) {
            if (musics.get(music) != null) {
                musics.get(music).dispose();
            }
        }
        currentMusic = null;
    }

    public void putSound(Sounds soundType, Sound sound) {
        SoundContainer soundCont = sounds.get(soundType);
        //Array<Sound> soundArr = sounds.get(soundType);
        if (soundCont == null) {
            soundCont = new SoundContainer();
        }

        soundCont.addSound(sound);
        sounds.put(soundType, soundCont);
    }

    public long playSound(Sounds soundOption) {
        if (shutUpYourFace) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        Sound s = soundCont.getSound();
        return (s != null) ? s.play(1f) : 0;
    }

    public void playMusic(Musics musicOption) {
        // Stop currently running music
        if (currentMusic != null && eCurrentMusic != musicOption) {
            setMusicVolume(0f, 1f);
            oldCurrentMusic = currentMusic;
            //currentMusic.stop();
        }

        boolean currMusicNull = currentMusic == null;
        // Set specified music track as current and play it
        currentMusic = musics.get(musicOption);
        eCurrentMusic = musicOption;
        currentMusic.setLooping(true);
        if (currMusicNull) {
            currentMusic.play();
        }
        //setMusicVolume(MUSIC_VOLUME, 1f);
        //currentMusic.play();
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void stopSound(Sounds soundOption) {
        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont != null) {
            soundCont.stopSound();
        }
    }

    public void stopAllSounds() {
        for (SoundContainer soundCont : sounds.values()) {
            if (soundCont != null) {
                soundCont.stopSound();
            }
        }
    }

    public void setMusicVolume(float level, float duration) {
        Tween.to(musicVolume, 1, duration).target(level).ease(Sine.IN).start(game.tween);
        /*LudumDare43.game.tween.killTarget(musicVolume);
        Tween.to(musicVolume, 1, duration)
                .target(level)
                .ease(Sine.IN)
                .start(LudumDare43.game.tween);*/
    }


    public Music.OnCompletionListener nextSong = new Music.OnCompletionListener() {
        @Override
        public void onCompletion(Music music) {
//            if (currentMusic == musics.get(Musics.SpaceAmbWithMaster)){
//                currentMusic = musics.get(Musics.SpaceDramaWithMaster);
//                eCurrentMusic = Musics.SpaceDramaWithMaster;
//            } else {
//                currentMusic = musics.get(Musics.RockHardyWithMaster);
//                eCurrentMusic = Musics.RockHardyWithMaster;
//            }
            currentMusic.setVolume(musicVolume.floatValue());
            currentMusic.play();
            currentMusic.setOnCompletionListener(nextSong);
        }
    };
}

class SoundContainer {
    public Array<Sound> sounds;
    public Sound currentSound;

    public SoundContainer() {
        sounds = new Array<Sound>();
    }

    public void addSound(Sound s) {
        if (!sounds.contains(s, false)) {
            sounds.add(s);
        }
    }

    public Sound getSound() {
        if (sounds.size > 0) {
            int randIndex = MathUtils.random(0, sounds.size - 1);
            Sound s = sounds.get(randIndex);
            currentSound = s;
            return s;
        } else {
            System.out.println("No sounds found!");
            return null;
        }
    }

    public void stopSound() {
        if (currentSound != null) {
            currentSound.stop();
        }
    }

    public void dispose() {
        if (currentSound != null) {
            currentSound.dispose();
        }
    }
}
