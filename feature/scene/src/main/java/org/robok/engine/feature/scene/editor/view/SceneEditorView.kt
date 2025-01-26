package org.robok.engine.feature.scene.editor.view

/*
 *  This file is part of Robok © 2024.
 *
 *  Robok is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Robok is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with Robok.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Cubemap
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector2
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.SceneSkybox
import net.mgsx.gltf.scene3d.utils.EnvironmentUtil
import org.robok.engine.feature.scene.editor.controller.CameraInputController2
import org.robok.engine.feature.scene.editor.drawing.DrawingRenderer
import org.robok.engine.feature.scene.editor.interfaces.ObjectActionListener
import org.robok.engine.feature.scene.editor.interfaces.ObjectListener
import org.robok.engine.feature.scene.editor.objects.ObjectCommand
import org.robok.engine.feature.scene.editor.objects.ObjectsCreator
import org.robok.engine.feature.scene.editor.objects.SceneObject
import org.robok.hdritocubemap.HdriToCubemap

class SceneEditorView : ApplicationAdapter(), ObjectListener, ObjectActionListener {

  companion object {
    @JvmStatic val sceneState = SceneState()
  }

  data class CameraState(var fov: Float = 60f, var width: Float = 0f, var height: Float = 0f)

  data class SceneState(var objects: MutableList<SceneObject> = mutableListOf())

  private val cameraState = CameraState()

  private lateinit var sceneManager: SceneManager
  private lateinit var camera: PerspectiveCamera
  private lateinit var environmentCubeMap: Cubemap
  private lateinit var skyBox: SceneSkybox
  private lateinit var brdfLut: Texture
  private lateinit var cameraInputController2: CameraInputController2
  private lateinit var drawingRenderer: DrawingRenderer
  private lateinit var modelBatch: ModelBatch
  private var progress = 0f

  var command: String? = null

  lateinit var objectListener: ObjectListener

  private fun init() {
    initCamera()
    initSky()
    initSceneManager()
    initController()
    initHdri()
  }

  private fun initCamera() {
    cameraState.width = Gdx.graphics.width.toFloat()
    cameraState.height = Gdx.graphics.height.toFloat()
    camera = PerspectiveCamera(cameraState.fov, cameraState.width, cameraState.height)
    camera.position.set(20f, 20f, 20f)
    camera.lookAt(0f, 0f, 0f)
    camera.near = 1f
    camera.far = 300f
  }

  private fun initSky() {
    environmentCubeMap =
      EnvironmentUtil.createCubemap(
        InternalFileHandleResolver(),
        "skyscene/sky_",
        ".png",
        EnvironmentUtil.FACE_NAMES_NEG_POS,
      )

    skyBox = SceneSkybox(environmentCubeMap)
  }

  private fun initSceneManager() {
    sceneManager = SceneManager()
    sceneManager.setCamera(camera)
    sceneManager.setAmbientLight(1f)
    brdfLut = Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"))
    sceneManager.environment.set(PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLut))
    sceneManager.setSkyBox(skyBox)
  }

  private fun initController() {
    cameraInputController2 = CameraInputController2(camera, this)
    Gdx.input.setInputProcessor(cameraInputController2)
  }

  private fun initHdri() {
    // test
    val inputPath = "/storage/emulated/0/hdri/sky.hdr"
    val outputPath = "/storage/emulated/0/hdri/output/"
    HdriToCubemap.convertHdriToCubemap(inputPath, outputPath)
  }

  /** ************ Methods from Object Listener ************* */
  override fun onTap(x: Float, y: Float, count: Int, button: Int) =
    objectListener.onTap(x, y, count, button)

  override fun onObjectClick(sceneObject: SceneObject, x: Float, y: Float) =
    objectListener.onObjectClick(sceneObject, x, y)

  override fun onZoom(initialDistance: Float, distance: Float) =
    objectListener.onZoom(initialDistance, distance)

  override fun onPinch(
    initialPointer1: Vector2,
    initialPointer2: Vector2,
    pointer1: Vector2,
    pointer2: Vector2,
  ) = objectListener.onPinch(initialPointer1, initialPointer2, pointer1, pointer2)

  override fun onTouchDown(x: Float, y: Float, count: Int, button: Int) =
    objectListener.onTouchDown(x, y, count, button)

  /** ************ End ************* */

  /** ************ Methods from Object Action Listener ************* */

  /** TODO: move object* */
  override fun onMove(x: Float, y: Float, z: Float) = Unit

  /** ************ End ************* */
  override fun create() {
    init()
    drawingRenderer = DrawingRenderer()
    modelBatch = ModelBatch()
    command = ObjectCommand.CREATE_CUBE
  }

  override fun render() {
    configureGDX()
    update()

    modelBatch.begin(camera)
    sceneManager.render()

    onTime()

    Gdx.gl.glEnable(GL30.GL_DEPTH_TEST)

    drawingRenderer.start(camera)

    cameraInputController2.updateRenderer(drawingRenderer.shapeRenderer)

    drawingRenderer.gridWithCircleAnimation(200f, 200f, 1f, 0.1f)
    drawingRenderer.drawRotatedSquare(0f, 4.5f, 2f, true)
    drawingRenderer.end()

    renderObjects()

    modelBatch.end()

    Gdx.gl.glDisable(GL30.GL_DEPTH_TEST)
  }

  override fun dispose() {
    sceneManager.dispose()
    environmentCubeMap.dispose()
    brdfLut.dispose()
    skyBox.dispose()
    modelBatch.dispose()
    disposeObjects()
  }

  private fun update() {
    val time = Gdx.graphics.deltaTime

    cameraInputController2.update()
    camera.update()
    sceneManager.update(time)
    cameraInputController2.update()
  }

  private fun configureGDX() {
    Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
    Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
    Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT or GL30.GL_DEPTH_BUFFER_BIT)
  }

  private fun onTime() {
    command?.let {
      invoke(it)
      command = null
    }
  }

  private fun renderObjects() {
    sceneState.objects.forEach { sceneObject -> modelBatch.render(sceneObject.modelInstance) }
  }

  private fun disposeObjects() {
    sceneState.objects.forEach { sceneObject -> sceneObject.modelInstance.model.dispose() }
  }

  private fun invoke(objectCommand: String) {
    try {
      val createObjects = ObjectsCreator(cameraInputController2, sceneState.objects)
      val clazz = createObjects::class.java
      val method = clazz.getDeclaredMethod(objectCommand)
      method.invoke(createObjects)
      sceneState.objects = createObjects.get()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
