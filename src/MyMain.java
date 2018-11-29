import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import soot.CompilationDeathException;
import soot.G;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Timers;
import soot.Transform;
import soot.options.Options;


public class MyMain {

	public static void main(String[] args) {
		inst = new MyMain();
		inst.run(args);
	}

	private int run(String[] args) {
		try {
			String[] cmdLineArgs;
			cmdLineArgs = args;
			
			try {
				processCmdLine(cmdLineArgs);
				
				Util.v().read_sc();
				Scene.v().loadNecessaryClasses();

				Options.v().set_output_format(1);
				Options.v().setPhaseOption("jb.ls", "enabled:true");
				Options.v().setPhaseOption("jb", "use-original-names:true");
			
				PackManager.v().runPacks();
				
				if(reachableClasses().getName().equals(Util.v().sc_className)){
					preProcess.v().process(reachableClasses());
					new PDG(reachableClasses());
				
					new reconstructCFG(reachableClasses());
				}
				else {
					System.err.println("Different class in slicing criteria.");
				}
				
			} catch (CompilationDeathException e) {
				if (e.getStatus() == CompilationDeathException.COMPILATION_ABORTED) {
					System.out.println("Compilation failed : " + e.getMessage());
				}
				return e.getStatus();
			}
			return CompilationDeathException.COMPILATION_SUCCEEDED;
		} catch (OutOfMemoryError e) {
			throw e;
		}
	}

	private SootClass reachableClasses() {
		return Scene.v().getApplicationClasses().getFirst();
    }
	
	private void processCmdLine(String[] args) {

        if (!Options.v().parse(args))
            throw new CompilationDeathException(
                CompilationDeathException.COMPILATION_ABORTED,
                "Option parse error");

        if( PackManager.v().onlyStandardPacks() ) {
            for( Iterator<Pack> packIt = PackManager.v().allPacks().iterator(); packIt.hasNext(); ) {
                final Pack pack = (Pack) packIt.next();
                Options.v().warnForeignPhase(pack.getPhaseName());
                for( Iterator<Transform> trIt = pack.iterator(); trIt.hasNext(); ) {
                    final Transform tr = (Transform) trIt.next();
                    Options.v().warnForeignPhase(tr.getPhaseName());
                }
            }
        }
        Options.v().warnNonexistentPhase();

        if (Options.v().help()) {
            G.v().out.println(Options.v().getUsage());
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if (Options.v().phase_list()) {
            G.v().out.println(Options.v().getPhaseList());
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if(!Options.v().phase_help().isEmpty()) {
            for( Iterator phaseIt = Options.v().phase_help().iterator(); phaseIt.hasNext(); ) {
                final String phase = (String) phaseIt.next();
                G.v().out.println(Options.v().getPhaseHelp(phase));
            }
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if (args.length == 0 || Options.v().version()) {
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        postCmdLineCheck();
    }
	
	private void postCmdLineCheck() {
		if (Options.v().classes().isEmpty() && Options.v().process_dir().isEmpty()) {
			throw new CompilationDeathException(
					CompilationDeathException.COMPILATION_ABORTED, 
					"No main class specified");
		}
	}

	private static MyMain inst;
}
