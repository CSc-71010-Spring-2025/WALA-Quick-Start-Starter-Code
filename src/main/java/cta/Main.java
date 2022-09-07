package cta;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.classLoader.Language;
import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.ClassLoaderReference;

public class Main {

	/**
	 * Location of the file with a list of packages to exclude from the analysis.
	 */
	private static final String EXCLUSIONS_FILE_PATH_NAME = "Java60RegressionExclusions.txt";

	public static void main(String[] args) throws Exception {
		String classPath = args[0];

		File exclusionsFile = new File(EXCLUSIONS_FILE_PATH_NAME);
		AnalysisScope scope = AnalysisScopeReader.instance.makeJavaBinaryAnalysisScope(classPath, exclusionsFile);

		ClassHierarchy hierarchy = ClassHierarchyFactory.make(scope);
		Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(hierarchy);

		AnalysisOptions options = new AnalysisOptions(scope, entrypoints);
		AnalysisCache cache = new AnalysisCacheImpl();

		CallGraphBuilder<?> builder = Util.makeZeroCFABuilder(Language.JAVA, options, cache, hierarchy);
		CallGraph graph = builder.makeCallGraph(options, null);

		graph.forEach(node -> {
			// get the IR for the call graph "node." Each node in this graph represents a
			// method.
			IR ir = node.getIR();

			if (ir != null) {
				// the method whose body the IR represents.
				IMethod method = ir.getMethod();

				// only process methods from classes in the "application" space (as opposed to
				// the libraries).
				if (method.getDeclaringClass().getClassLoader().getReference()
						.equals(ClassLoaderReference.Application)) {
					System.out.println("Processing instructions for application method: " + method);

					// the instructions in the IR.
					SSAInstruction[] instructions = ir.getInstructions();

					// "check" each one.
					Arrays.stream(instructions).filter(Objects::nonNull)
							.forEach(instruction -> checkInstruction(instruction));
				}
			}
		});
	}

	/**
	 * Check each instruction. TODO: Your changes should go in this method.
	 *
	 * @param instruction
	 *            The instruction to be checked.
	 */
	private static void checkInstruction(SSAInstruction instruction) {
		System.out.println(instruction);
	}
}
