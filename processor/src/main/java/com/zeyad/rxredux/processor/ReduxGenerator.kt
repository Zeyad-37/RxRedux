package com.zeyad.rxredux.processor

import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.zeyad.rxredux.annotations.Event
import com.zeyad.rxredux.annotations.Transition
import com.zeyad.rxredux.annotations.Vertex
import com.zeyad.rxredux.annotations.ViewModel

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

@SupportedAnnotationTypes("com.zeyad.rxredux.annotations.Vertex", "com.zeyad.rxredux.annotations.Transition", "com.zeyad.rxredux.annotations.Event", "com.zeyad.rxredux.annotations.ViewModel")
@AutoService(Processor::class)
class ReduxGenerator : AbstractProcessor() {
    override fun process(annotations: Set<TypeElement>, env: RoundEnvironment): Boolean {
        val vertexElements = env.getElementsAnnotatedWith(Vertex::class.java)
        val transitionElements = env.getElementsAnnotatedWith(Transition::class.java)
        val eventElements = env.getElementsAnnotatedWith(Event::class.java)
        val viewModelElements = env.getElementsAnnotatedWith(ViewModel::class.java)



        env.getElementsAnnotatedWith(Vertex::class.java).map {

        }
//
//
//        val types = ImmutableList.Builder()
//                .addAll(ElementFilter.typesIn(vertexElements))
//                .build()
//        for (type in vertexElements) {
//            processType(type)
//        }
        // We are the only ones handling AutoParcel annotations
        return true
    }

    private fun processType(type: TypeElement) {
        //        String className = generatedSubclassName(type);
        //        String source = generateClass(type, className);
        //        writeSourceFile(className, source, type);
    }

    private fun writeSourceFile(className: String, text: String, originatingType: TypeElement) {
        //        try {
        //            JavaFileObject sourceFile =
        //                    processingEnv.getFiler().
        //                            createSourceFile(className, originatingType);
        //            Writer writer = sourceFile.openWriter();
        //            try {
        //                writer.write(text);
        //            } finally {
        //                writer.close();
        //            }
        //        } catch (IOException e) {// silent}
        //        }
    }
}
