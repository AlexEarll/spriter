/**************************************************************************
 * Copyright 2013 by Trixt0r
 * (https://github.com/Trixt0r, Heinrich Reich, e-mail: trixter16@web.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
***************************************************************************/

package com.brashmonkey.spriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.brashmonkey.spriter.animation.SpriterAnimation;
import com.brashmonkey.spriter.animation.SpriterKeyFrame;
import com.brashmonkey.spriter.file.Reference;
import com.brashmonkey.spriter.mergers.SpriterAnimationBuilder;
import com.brashmonkey.spriter.objects.SpriterBone;
import com.brashmonkey.spriter.objects.SpriterObject;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.discobeard.spriter.dom.Animation;
import com.discobeard.spriter.dom.Entity;
import com.discobeard.spriter.dom.File;
import com.discobeard.spriter.dom.SpriterData;

/**
 * This class provides the {@link #generateKeyFramePool(SpriterData)} method to generate all necessary data which {@link SpriterPlayer} needs to animate.
 * It is highly recommended to call this method only once for a SCML file since {@link SpriterPlayer} does not modify the data you pass through the
 * constructor and also to save memory.
 * 
 * @author Trixt0r
 *
 */

public class SpriterKeyFrameProvider {
	
	/**
	 * Generates all needed keyframes from the given spriter data. This method sorts all objects by its z_index value to draw them in a proper way.
	 * @param spriterData SpriterData to generate from.
	 * @return generated keyframe list.
	 */
	public static List<SpriterAnimation> generateKeyFramePool(SpriterData data, Entity entity){
		List<SpriterAnimation> spriterAnimations = new ArrayList<SpriterAnimation>();
		List<Animation> animations = entity.getAnimation();
		SpriterAnimationBuilder frameBuilder = new SpriterAnimationBuilder();
		for(Animation anim: animations){
			SpriterAnimation spriterAnimation = frameBuilder.buildAnimation(anim);
			for(SpriterKeyFrame key: spriterAnimation.frames){
				Arrays.sort(key.getObjects());
				for(SpriterBone bone: key.getBones()){
					bone.info = entity.getInfo(bone.getName());
					for(SpriterBone bone2: key.getBones()){
						if(bone2.getParentId() != null)
							if(!bone2.equals(bone) && bone2.getParentId() == bone.getId())
								bone.addChildBone(bone2);
					}
					for(SpriterObject object: key.getObjects()){
						if(object.info == null)	object.info = entity.getInfo(object.getName());
						Reference ref = object.getRef();
						if(ref.folder != -1 && ref.file != -1){
							File f = data.getFolder().get(ref.folder).getFile().get(ref.file);
							ref.dimensions = new SpriterRectangle(0, f.getHeight(), f.getWidth(), 0f);
						} else ref.dimensions = new SpriterRectangle(0, object.info.height, object.info.width, 0f);
						if(bone.getId()== object.getParentId())
							bone.addChildObject(object);
					}
				}
			}
			spriterAnimations.add(spriterAnimation);
		}
		return spriterAnimations;
	}
}
