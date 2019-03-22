package dvoraka.archbuilder.data

import dvoraka.archbuilder.template.TemplateHelper
import dvoraka.archbuilder.util.JavaUtils
import spock.lang.Specification

class DirectorySpec extends Specification implements TemplateHelper {

    def "get package name"() {
        setup:
            String basePkgPath = 'test/abc/efg'
            String basePkgName = JavaUtils.path2pkg(basePkgPath)

            Directory root = root('testDir')
            Directory srcBase = srcRootAndBase(root, basePkgPath)

            String dir1pkgPath = 'dir1'
            String dir1pkgName = dir1pkgPath
            Directory dir1 = new Directory.Builder(dir1pkgPath, DirType.CUSTOM_TYPE)
                    .filename('abc')
                    .parent(srcBase)
                    .build()

            String dir2pkgPath = 'dir2/package1/subpackage'
            String dir2pkgName = JavaUtils.path2pkg(dir2pkgPath)
            Directory dir2 = new Directory.Builder(dir2pkgPath, DirType.CUSTOM_TYPE)
                    .filename('def')
                    .parent(srcBase)
                    .build()

        expect:
            basePkgName == srcBase.getPackageName()

            "$basePkgName.$dir1pkgName" == dir1.getPackageName()
            "$basePkgName.$dir2pkgName" == dir2.getPackageName()
    }
}
