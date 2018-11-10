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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		inst = new MyMain();
		inst.run(args);
		System.out.println("aa");
	}

	private void run(String[] args) {
		try {
			String[] cmdLineArgs;
			Date start, finish;
			
			cmdLineArgs = args;
			start = new Date();
			
			try {
				Timers.v().totalTimer.start();
				
				processCmdLine(cmdLineArgs);
				
				Util.v().read_sc();
				
				G.v().out.println("Soot has started on "+start);
				
				Scene.v().loadNecessaryClasses();
				
				
				Options.v().set_output_format(1);
				Options.v().setPhaseOption("jb.ls", "enabled:true");
				Options.v().setPhaseOption("jb", "use-original-names:true");
			
				//int defFormat = Options.v().output_format();
				//System.out.println(defFormat);
				PackManager.v().runPacks();
				
				/*Iterator itr = reachableClasses();
				while (itr.hasNext()) {
					SootClass scl = (SootClass)itr.next();
					System.out.println(scl.getName());
					
				}*/
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private Iterator<SootClass> reachableClasses() {

        return Scene.v().getApplicationClasses().iterator();

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
            printVersion();
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        postCmdLineCheck();
    }
	
	private void printVersion() {
		
	}

	private void postCmdLineCheck() {
		if (Options.v().classes().isEmpty() && Options.v().process_dir().isEmpty()) {
			throw new CompilationDeathException(
					CompilationDeathException.COMPILATION_ABORTED, 
					"No main class specified");
		}
	}

	private void exitCompilation(int status) {
        exitCompilation(status, "");
    }

    private void exitCompilation(int status, String msg) {
        if(status == CompilationDeathException.COMPILATION_ABORTED) {
                G.v().out.println("compilation failed: "+msg);
        }
    }
    
	private static MyMain inst;
	
	private MyMain() {
			
	}
}
